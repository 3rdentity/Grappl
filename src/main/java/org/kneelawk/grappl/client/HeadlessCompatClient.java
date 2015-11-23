package org.kneelawk.grappl.client;

import io.grappl.client.api.Grappl;
import io.grappl.client.impl.Application;
import io.grappl.client.api.ApplicationMode;
import io.grappl.client.impl.error.AuthenticationFailureException;
import io.grappl.client.impl.stable.GrapplBuilder;
import io.grappl.client.impl.error.RelayServerNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

/*
 * This Class is not part of the original grappl client.
 */

/**
 * This class contains the main method for starting grappl with more control in
 * a headless environment.
 */
public class HeadlessCompatClient {
	public static final int FLAG_PORT = 0;
	public static final int FLAG_SERVER = 1;
	public static final int FLAG_USERNAME = 2;
	public static final int FLAG_PASSWORD = 3;
	public static final int FLAG_OUTPUT = 4;

	public static LinkedList<Integer> foundFlags;

	public static int port = 80;
	public static String server = "n.grappl.io";
	public static String username = null;
	public static String password = null;
	public static File outputFile = null;

	public static void main(String[] args) {
		Application.create(args, ApplicationMode.NOGUI);
		foundFlags = new LinkedList<Integer>();
		if (args.length > 0) {
			// parse every argument
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					if (args[i].startsWith("--")) {
						if ("--help".equals(args[i])) {
							printHelp();
						} else if ("--port".equals(args[i])) {
							if (args.length <= i + 1) {
								System.err.println("Error parsing argument: "
										+ args[i] + ", missing parameters!");
								tryHelp();
							} else {
								try {
									port = Integer.parseInt(args[++i]);
								} catch (NumberFormatException e) {
									System.err.println("Error parsing number: "
											+ args[i] + " as argument for: "
											+ args[i - 1]);
									tryHelp();
								}
							}
						} else if ("--server".equals(args[i])) {
							if (args.length <= i + 1) {
								System.err.println("Error parsing argument: "
										+ args[i] + ", missing parameters!");
								tryHelp();
							} else {
								server = args[++i];
							}
						} else if ("--username".equals(args[i])) {
							if (args.length <= i + 1) {
								System.err.println("Error parsing argument: "
										+ args[i] + ", missing parameters!");
								tryHelp();
							} else {
								username = args[++i];
							}
						} else if ("--password".equals(args[i])) {
							if (args.length <= i + 1) {
								System.err.println("Error parsing argument: "
										+ args[i] + ", missing parameters!");
								tryHelp();
							} else {
								password = args[++i];
							}
						} else if ("--output".equals(args[i])) {
							if (args.length <= i + 1) {
								System.err.println("Error parsing argument: "
										+ args[i] + ", missing parameters!");
								tryHelp();
							} else {
								outputFile = new File(args[++i]);
							}
						} else {
							System.err.println("Unknown argument: " + args[i]);
							tryHelp();
						}
					} else {
						parseCharArgs(args[i]);
					}
				} else {
					if (foundFlags.isEmpty()) {
						System.err.println("Unidentified argument: " + args[i]);
						tryHelp();
					} else {
						int flag = foundFlags.poll().intValue();
						switch (flag) {
						case FLAG_PORT:
							try {
								port = Integer.parseInt(args[i]);
							} catch (NumberFormatException e) {
								System.err.println("Error parsing number: "
										+ args[i] + " as argument for: -p");
								tryHelp();
							}
							break;
						case FLAG_SERVER:
							server = args[i];
							break;
						case FLAG_USERNAME:
							username = args[i];
							break;
						case FLAG_PASSWORD:
							password = args[i];
							break;
						case FLAG_OUTPUT:
							outputFile = new File(args[i]);
							break;
						default:
							System.err.println("Unknown argument flag: " + flag
									+ "! This usually indicates a bug!");
						}
					}
				}
			}

			startGrappl(port, server, username, password, outputFile);
		} else {
			Application.getCommandHandler().createConsoleCommandListenThread();
		}
	}

	public static void printHelp() {
		System.out
				.println("Grappl Client (Headless Compat Mode) Command Line Options:");
		System.out.println("GrapplClient.jar [-psuPo] <OPTION ARGS>");
		System.out
				.println("GrapplClient.jar [--port <PORT>] [--server <SERVER>]");
		System.out
				.println("                 [--username <USERNAME> --password <PASSWORD>]");
		System.out.println("                 [--output <OUTPUT>]");
		System.out.println();
		System.out.println("--- Options ---");
		System.out
				.println("\t-p\t--port <PORT>\t\tSet the port that grappl will re-route to to PORT.");
		System.out
				.println("\t-s\t--server <SERVER>\tSet the server grappl will re-route from to SERVER.");
		System.out
				.println("\t-u\t--username <USERNAME>\tSet the username to USERNAME for authentication.");
		System.out
				.println("\t-P\t--password <PASSWORD>\tSet the password to PASSWORD for authentication.");
		System.out
				.println("\t-o\t--output <OUTPUT>\tWrite the external grappl server port to OUTPUT file.");
		System.exit(0);
	}

	public static void tryHelp() {
		System.err.println("Try --help for a list of valid arguments.");
	}

	public static void parseCharArgs(String arg) {
		for (int i = 1; i < arg.length(); i++) {
			char c = arg.charAt(i);
			switch (c) {
			case 'h':
				printHelp();
				break;
			case 'p':
				foundFlags.add(Integer.valueOf(FLAG_PORT));
				break;
			case 's':
				foundFlags.add(Integer.valueOf(FLAG_SERVER));
				break;
			case 'u':
				foundFlags.add(Integer.valueOf(FLAG_USERNAME));
				break;
			case 'P':
				foundFlags.add(Integer.valueOf(FLAG_PASSWORD));
				break;
			case 'o':
				foundFlags.add(Integer.valueOf(FLAG_OUTPUT));
				break;
			default:
				System.err.println("Unknown shortened argument: -" + c);
				System.err.println("Try --help for a list of valid arguments.");
			}
		}
	}

	public static void startGrappl(int port, String server, String username,
			String password, File outputFile) {
		GrapplBuilder builder = new GrapplBuilder();
		builder.atLocalPort(port);
		if (username != null && !"".equals(username) && password != null
				&& !"".equals(password)) {
            try {
                builder.login(username, password.toCharArray(), null);
            } catch (AuthenticationFailureException e) {
                e.printStackTrace();
            }
        }
		Grappl grappl = builder.build();
        try {
            grappl.connect(server);
        } catch (RelayServerNotFoundException e) {
            e.printStackTrace();
        }
        if (outputFile != null) {
			try {
				PrintWriter w = new PrintWriter(outputFile);
				w.print(grappl.getExternalServer().getPort());
				w.flush();
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

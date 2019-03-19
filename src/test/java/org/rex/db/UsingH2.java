package org.rex.db;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * 启动和停止H2
 */
public class UsingH2 {
	
    private static Server server;

    @BeforeClass
	public static void start() {
		try {
			System.out.println("starting h2 on port 9092...");
			server = Server.createTcpServer(new String[] { "-tcp", "-tcpAllowOthers", "-tcpPort", "9092" }).start();
			System.out.println("started h2:  " + server.getStatus());
		} catch (SQLException e) {
			System.out.println("error: " + e.toString());

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

    @AfterClass
	public static void stop() {
		if (server != null) {
			System.out.println("closing h2...");
			server.stop();
			System.out.println("h2 closed.");
		}
	}
    
}

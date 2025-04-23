package com.hubis.acs;

import com.hubis.acs.common.configuration.customAnnotation.EnableAllprotocols;
import com.hubis.acs.common.configuration.protocol.ProtocolConfig;
import com.hubis.acs.service.AlgorithmService;
import com.hubis.acs.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.hubis.acs") // ✅ 전체 패키지를 포함하여 스캔
@EnableTransactionManagement
@EnableAllprotocols
@EnableScheduling
public class DemoApplication implements CommandLineRunner {

	private static final String LOG_DIR = "C:/logs";

	@Autowired
	AlgorithmService algorithmService;

	@Autowired
	TransferService transferService;

	@Autowired
	ApplicationContext context;

	public static void main(String[] args) {
		// 로그 디렉토리 생성
		createLogDirectories();
		System.setProperty("javax.net.debug", "ssl,handshake,certpath");
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(DemoApplication.class, args);
	}

	private static void createLogDirectories() {
		try {
			Files.createDirectories(Paths.get(LOG_DIR));
			Files.createDirectories(Paths.get(LOG_DIR + "/archive/app"));
			Files.createDirectories(Paths.get(LOG_DIR + "/archive/boot"));
			Files.createDirectories(Paths.get(LOG_DIR + "/archive/error"));
		} catch (IOException e) {
			System.err.println("Failed to create log directories: " + e.getMessage());
		}
	}

	@Override
	public void run(String... args) throws Exception {
//		 UMLGenerator.generateProjectUML(context,
//		 		"com.hubis.acs",  // 프로젝트의 base package
//		 		"C:\\HUBIS\\ROBOTICS\\HU_ACS\\acs\\src\\main\\resources\\uml-diagram.puml"  // 출력 파일 경로
//		 );

		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("명령어를 입력하세요 / 도움말 help");
			String input = scanner.nextLine().trim();
			String[] commands = input.split(" ");

			if (commands[0].equalsIgnoreCase("exit")) {
				System.out.println("프로그램을 종료합니다.");
				break;
			} else if (commands[0].equalsIgnoreCase("help")) {
				System.out.println("사용 가능한 명령어:");
				System.out.println("1. transfer agentCount - 임의의 데이터로 시뮬레이션합니다.");
				System.out.println("2. exit - 프로그램을 종료합니다.");
			} else if(commands[0].equalsIgnoreCase("transfer")) {
				int agentCount = Integer.parseInt(commands[1]);
				transferService.pathFinding(agentCount);
			} else {
				System.out.println("잘못된 명령어입니다. 'help'를 입력하여 사용 가능한 명령어를 확인하세요.");
			}
		}
		scanner.close();
	}

}
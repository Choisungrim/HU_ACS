package com.hubis.acs;

import com.hubis.acs.repository.GlobalPathFinder;
import com.hubis.acs.repository.LocalPathPlanner;
import com.hubis.acs.repository.Node;
import com.hubis.acs.service.AlgorithmService;
import com.hubis.acs.service.TransferService;
import com.hubis.acs.service.impl.AlgorithmServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.hubis.acs")
public class DemoApplication implements CommandLineRunner {

	private static final String LOG_DIR = "C:/logs";

	@Autowired
	AlgorithmService algorithmService;

	@Autowired
	TransferService transferService;

	public static void main(String[] args) {
		// 로그 디렉토리 생성
		createLogDirectories();
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
		Scanner scanner = new Scanner(System.in);
		GlobalPathFinder globalPathFinder = new GlobalPathFinder();
		LocalPathPlanner localPathPlanner = new LocalPathPlanner(new boolean[10][10]); // 예시 환경

		while (true) {
			System.out.println("명령어를 입력하세요 / 도움말 help");
			String input = scanner.nextLine().trim();
			String[] commands = input.split(" ");

			if (commands[0].equalsIgnoreCase("find") && commands.length == 5) {
				int startX = Integer.parseInt(commands[1]);
				int startY = Integer.parseInt(commands[2]);
				int goalX = Integer.parseInt(commands[3]);
				int goalY = Integer.parseInt(commands[4]);

				List<Node> globalPath = globalPathFinder.findGlobalPath(startX, startY, goalX, goalY, new boolean[10][10]);
				//List<Node> localPath = localPathPlanner.planLocalPath(globalPath, new ArrayList<>(), "",globalPath.get(/));

				System.out.println("Global Path: " + globalPath);
				//System.out.println("Local Path: " + localPath);
			} else if (commands[0].equalsIgnoreCase("exit")) {
				System.out.println("프로그램을 종료합니다.");
				break;
			} else if (commands[0].equalsIgnoreCase("help")) {
				System.out.println("사용 가능한 명령어:");
				System.out.println("1. find startX startY goalX goalY - 시작 좌표와 목표 좌표를 입력하여 경로를 찾습니다.");
				System.out.println("2. transfer agentCount - 임의의 데이터로 시뮬레이션합니다.");
				System.out.println("3. exit - 프로그램을 종료합니다.");
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Curatare {
	// Clasa folosita pentru a retine o pereche (nod, distanta).
	private static class Pair {
		Node node;
		Integer dist;

		public Pair(Node node, Integer dist) {
			this.node = node;
			this.dist = dist;
		}
	}
	// Clasa folosita pentru a reprezenta un nod (dirty spot sau robot).
	private static class Node {
		int x;
		int y;
		boolean isRobot;
		// Lista de adiacenta a nodului curent.
		ArrayList<Pair> dist = new ArrayList<>();

		public Node(int x, int y, boolean isRobot) {
			this.x = x;
			this.y = y;
			this.isRobot = isRobot;
		}

		// Functie care intoarce distana dintre nodul curent si un alt nod, primit ca parametru.
		public int getDistance(Node node) {
			for (Pair pair : dist) {
				if (node.equals(pair.node)) {
					return pair.dist;
				}
			}
			// Daca nodul nu are legatura cu nodul primit, distanta este maxima.
			return Integer.MAX_VALUE;
		}

		// Doua noduri sunt egale atunci cand au aceleasi coordonate.
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Node node = (Node) o;
			return x == node.x && y == node.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y, isRobot, dist);
		}
	}

	// Clasa folosita pentru a stoca pasul curent si coordonatele unui nod. Folosita in BFS.
	private static class BfsPair {
		int step;
		int x;
		int y;

		public BfsPair(int step, int x, int y) {
			this.step = step;
			this.x = x;
			this.y = y;
		}
	}

	static int n;
	static int m;
	static char[][] map;

	static Integer result = Integer.MAX_VALUE;

	// Lista cu toate nodurile.
	static ArrayList<Node> nodes = new ArrayList<>();

	// Functie care citeste datele din fisierul de intrare.
	private static void read() {
		try {
			File in = new File("curatare.in");
			Scanner scan = new Scanner(in);
			n = scan.nextInt();
			m = scan.nextInt();
			scan.nextLine();
			map = new char[n + 1][m + 1];
			for (int i = 0; i < n; i++) {
				map[i] = scan.nextLine().toCharArray();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Read file not found");
			e.printStackTrace();
		}
	}

	// Functie care scrie rezultatele in fisierul de iesire.
	private static void write() {
		try {
			FileWriter out = new FileWriter("curatare.out");
			out.write(result.toString());
			out.close();
		} catch (IOException e) {
			System.out.println("Write file error");
			e.printStackTrace();
		}
	}

	// Functie care populeaza nodurile in functie de harta.
	private static void populateNodes() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				// Verific daca pe pozitia curenta se afla un dirty spot sau un robot.
				if (map[i][j] == 'S' || map[i][j] == 'R') {
					Node newNode;
					// Creez noul nod.
					if (map[i][j] == 'S') {
						newNode = new Node(i, j, false);
					} else {
						newNode = new Node(i, j, true);
					}
					// Creez legatura intre noul nod si fiecare nod deja existent.
					for (Node node : nodes) {
						node.dist.add(new Pair(newNode, Integer.MAX_VALUE));
						newNode.dist.add(new Pair(node, Integer.MAX_VALUE));
					}
					// Adaug noul nod in lista de noduri.
					newNode.dist.add(new Pair(newNode, 0));
					nodes.add(newNode);
				}
			}
		}
	}

	// Functie care verifica daca o pozitie se afla inauntrul matricei.
	private static boolean isInside(int x, int y) {
		return x >= 0 && x < n && y >= 0 && y < m;
	}

	// Functie care intoarce nodul aflat la coordonatele date ca parametru.
	private static Node getNode(int x, int y) {
		for (Node node : nodes) {
			if (node.x == x && node.y == y) {
				return node;
			}
		}
		return null;
	}

	// Functie care calculeaza distantele de la un nod (sursa) la toate celelalte, folosind un BFS.
	private static void calculateDistances(Node node) {
		final int[] dx = {-1, 0, 1, 0};
		final int[] dy = {0, 1, 0, -1};

		Queue<BfsPair> queue = new LinkedList<>();
		boolean[][] visited = new boolean[n + 1][m + 1];

		// Adaug primul nod in coada.
		visited[node.x][node.y] = true;
		queue.add(new BfsPair(0, node.x, node.y));

		while (!queue.isEmpty()) {
			// Extrag primul element din coada.
			BfsPair current = queue.poll();
			Node currentNode = getNode(current.x, current.y);

			if (currentNode != null) {
				for (Pair pair : node.dist) {
					// Verific daca nodul curent este unul dintre nodurile la care are legatura
					// sursa, caz in care actualizez distanta pana la acesta.
					if (pair.node.equals(currentNode)) {
						pair.dist = current.step;
					}
				}
			}

			// Parcurg cei 4 vecini din matrice.
			for (int i = 0; i < 4; i++) {
				int nextX = current.x + dx[i];
				int nextY = current.y + dy[i];
				// Pentru fiecare vecin verific daca este inauntrul matricei, daca nu l-am vizitat
				// deja si daca nu este perete.
				if (isInside(nextX, nextY) && !visited[nextX][nextY] && map[nextX][nextY] != 'X') {
					// In acest caz, adaug o noua intrare in coada, cu pasul incrementat.
					queue.add(new BfsPair(current.step + 1, nextX, nextY));
					visited[nextX][nextY] = true;
				}
			}
		}
	}
	// Functie care genereaza toate alocarile de dirty spot-uri posibile pentru roboti,
	// folosind backtracking. Parametrul idx poate fi interpretat drept indicele dirty spot-ului,
	// sau drept cate dirty spot-uri au fost folosite pentru generarea alocarilor
	private static List<List<List<Node>>> allocateSpots(int idx, List<Node> dirtySpots,
														int robotCount) {
		// Rezultatul care reprezinta: o lista de alocari, fiecare alocare fiind o lista de roboti,
		// fiecare robot avand o lista de dirty spot-uri.
		List<List<List<Node>>> result = new ArrayList<>();
		Node spot = dirtySpots.get(idx);
		// Daca indicele este 0, inseamna ca trebuie sa generez alocarile
		// pentru un singur dirty spot.
		if (idx == 0) {
			// Vom avea robotCount alocari.
			for (int i = 0; i < robotCount; i++) {
				List<List<Node>> currList = new ArrayList<>();
				// In fiecare alocare vom avea robotCount roboti.
				for (int j = 0; j < robotCount; j++) {
					// Daca indicele robotului este egal cu indicele alocarii,
					// vom adauga dirty spot-ul in lista robotului. Astfel, se genereaza alocari
					// incat fiecare robot sa aiba alocat dirty spot-ul intr-o alocare.
					List<Node> currRobotSpots = new ArrayList<>();
					if (i == j) {
						currRobotSpots.add(spot);
					}
					currList.add(currRobotSpots);
				}
				result.add(currList);
			}
			return result;
		} else {
			// Altfel, preiau alocarile pentru pentru dirty spot-urile precedente
			// (apeland functia cu indicele decrementat).
			List<List<List<Node>>> prevResult = allocateSpots(idx - 1, dirtySpots, robotCount);
			// Parcurg alocarile.
			for (List<List<Node>> prevList : prevResult) {
				// Pentru fiecare alocare, am robotCount alocari noi, in functie de robotul caruia
				// ii aloc noul dirty spot.
				for (int i = 0; i < robotCount; i++) {
					List<List<Node>> currList = new ArrayList<>();
					// Copiez listele robotilor din alocarea precedenta in alocarea noua.
					for (List<Node> prevRobotSpots : prevList) {
						List<Node> currRobotSpots = new ArrayList<>(prevRobotSpots);
						currList.add(currRobotSpots);
					}
					// Adaug noul nod.
					currList.get(i).add(spot);
					// Adaug alocarea.
					result.add(currList);
				}
			}
			return result;
		}
	}

	// Functie care calculeaza distanta maxima pe care o are de parcurs un robot
	// in alocarea primita ca parametru.
	private static int getLongestDistance(List<Node> robots, List<List<Node>> spotAllocation) {
		int maxDist = Integer.MIN_VALUE;
		int robotCount = 0;
		// Calculez distanta pentru fiecare robot.
		for (List<Node> spots : spotAllocation) {
			Node robot = robots.get(robotCount);
			int dist = 0;
			// Parcurg lista de dirsty spot-uri si adaug la fiecare pas distanta dintre doua noduri
			// consecutive, incepand cu robot-ul.
			List<Node> remaining = new ArrayList<>(spots);
			Node current = robot;
			while (!remaining.isEmpty()) {
				remaining.sort(Comparator.comparingInt(current::getDistance));
				Node chosen = remaining.get(0);
				dist += current.getDistance(chosen);
				current = chosen;
				remaining.remove(0);
			}
			if (dist > maxDist) {
				maxDist = dist;
			}
			robotCount++;
		}
		return maxDist;
	}
	private static void solve() {
		// Populez nodurile.
		populateNodes();
		// Calculez distantele pentru toate nodurile.
		for (Node node : nodes) {
			calculateDistances(node);
		}

		// Impart lista de noduri in doua liste, una pentru dirty spot-uri si una pentru roboti.
		List<Node> dirtySpots = new ArrayList<>();
		List<Node> robots = new ArrayList<>();
		for (Node node : nodes) {
			if (!node.isRobot) {
				dirtySpots.add(node);
			} else {
				robots.add(node);
			}
		}
		// Generez toate alocarile.
		List<List<List<Node>>> allocations = allocateSpots(dirtySpots.size() - 1,
				dirtySpots, robots.size());
		// Pentru fiecare alocare calculez distanta maxima a unui robot, sortez distantele,
		// iar rezultatul este primul element (distanta minima).
		List<Integer> distances = allocations.stream()
				.map(l -> getLongestDistance(robots, l))
				.sorted(Integer::compareTo)
				.collect(Collectors.toList());
		result = distances.get(0);
	}

	public static void main(String[] args) {
		read();
		solve();
		write();
	}
}

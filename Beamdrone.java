import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Beamdrone {
	// Clasa folosita pentru a reprezenta un punct.
	private static class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	// Clasa folosita pentru a reprezenta un nod pentru algoritmul lui Dijkstra.
	private static class Node {
		Point node;
		// Directia prin care am ajung la nodul curent.
		int prevDir;
		// Distanta pana la nodul curent.
		int dist;

		public Node(Point node, int prevDir, int dist) {
			this.node = node;
			this.prevDir = prevDir;
			this.dist = dist;
		}
	}

	static int n;
	static int m;
	static Point start;
	static Point finish;
	static char[][] map;

	static Integer result = Integer.MAX_VALUE;

	// Matrice de distante.
	static int[][] dist;
	static final int[] dx = {-1, 0, 1, 0};
	static final int[] dy = {0, 1, 0, -1};

	// Functie care citeste datele din fisierul de intrare.
	private static void read() {
		try {
			File in = new File("beamdrone.in");
			Scanner scan = new Scanner(in);
			n = scan.nextInt();
			m = scan.nextInt();
			scan.nextLine();
			start = new Point(scan.nextInt(), scan.nextInt());
			finish = new Point(scan.nextInt(), scan.nextInt());
			scan.nextLine();

			map = new char[n + 1][m + 1];
			dist = new int[n][m];
			for (int i = 0; i < n; i++) {
				map[i] = scan.nextLine().toCharArray();
				// Initializez matricea de distante.
				for (int j = 0; j < m; j++) {
					dist[i][j] = Integer.MAX_VALUE;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Read file not found");
			e.printStackTrace();
		}
	}

	// Functie care scrie rezultatele in fisierul de iesire.
	private static void write() {
		try {
			FileWriter out = new FileWriter("beamdrone.out");
			out.write(result.toString());
			out.close();
		} catch (IOException e) {
			System.out.println("Write file error");
			e.printStackTrace();
		}
	}

	// Functie care calculeaza directia opusa.
	private static int oppositeDir(int dir) {
		return (dir + 2) % 4;
	}

	// Functie care verifica daca un punct se afla inauntrul matricei.
	private static boolean isInside(int x, int y) {
		return x >= 0 && x < n && y >= 0 && y < m;
	}
	private static void solve() {
		// Distanta pana la sursa este 0.
		dist[start.x][start.y] = 0;

		// Folosesc o coada de prioritati sortata dupa distanta de la sursa pana la nod.
		PriorityQueue<Node> queue = new PriorityQueue<>(1, Comparator.comparingInt(n -> n.dist));
		// Adaug sursa in coada cu directia precedenta -1 (adica sunt valide toate directiile).
		queue.add(new Node(start, -1, 0));

		while (!queue.isEmpty()) {
			// Extrag primul element.
			Node current = queue.poll();
			// Daca distanta din matricea de distante a devenit mai mica decat cea din nod,
			// trec mai departe.
			if (dist[current.node.x][current.node.y] < current.dist) {
				continue;
			}
			// Parcurg cele 4 directii.
			for (int i = 0; i < 4; i++) {
				// Daca directia curenta este opusa fata de cea a nodului extras, trec la
				// urmatoarea directie intrucat nu ar avea sens sa o iau inapoi si sa adaug
				// 2 secunde la distanta.
				if (current.prevDir == oppositeDir(i)) {
					continue;
				}
				int newX = current.node.x + dx[i];
				int newY = current.node.y + dy[i];
				// Verific daca pozitia vecinului este in matrice si este loc liber.
				if (isInside(newX, newY) && map[newX][newY] == '.') {
					int newDist;
					// Daca directia precedenta coincide cu cea actuala (sau daca directia
					// precedenta este -1) atunci distanta ramane la fel.
					if (current.prevDir == i || current.prevDir == -1) {
						newDist = 0;
					// Altfel, distanta creste cu 1 secunda.
					} else {
						newDist = 1;
					}
					// Daca distanta curenta impreuna cu noua distanta (0 sau 1 secunda) este
					// mai mica sau egala cu cea actuala a vecinului, aceasta este actualizata
					// si se adauga o noua intrare in matrice. Motivul pentru care folosesc '<='
					// este ca sa acopar cazul in care intr-o casuta se poate ajunge in mai multe
					// variante (directii diferite) dar cu aceeasi distanta.
					if (current.dist + newDist <= dist[newX][newY]) {
						dist[newX][newY] = current.dist + newDist;
						queue.add(new Node(new Point(newX, newY), i, dist[newX][newY]));
					}
				}
			}
		}

		result = dist[finish.x][finish.y];
	}

	public static void main(String[] args) {
		read();
		solve();
		write();
	}
}

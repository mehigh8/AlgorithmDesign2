import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Fortificatii {
	// Clasa folosita pentru a retine o pereche (nod, cost) pentru a reprezenta o muchie.
	private static class Pair {
		int node;
		long cost;

		public Pair(int node, long cost) {
			this.node = node;
			this.cost = cost;
		}
	}

	static int n;
	static int m;
	// Numarul de fortificatii disponibile.
	static int fortifications;
	// Listele de adiacenta.
	static List<Pair>[] adj;
	static boolean[] barbarianNodes;
	static Long result = Long.MAX_VALUE;

	// Vector de distante de la nodul 1.
	static long[] dist;

	// Functie care citeste datele din fisierul de intrare.
	private static void read() {
		try {
			File in = new File("fortificatii.in");
			Scanner scan = new Scanner(in);
			n = scan.nextInt();
			dist = new long[n + 1];
			barbarianNodes = new boolean[n + 1];
			// Initializez listele de adiacenta si vectorul de distante.
			adj = new List[n + 1];
			for (int i = 1; i <= n; i++) {
				dist[i] = Long.MAX_VALUE;
				adj[i] = new ArrayList<>();
			}
			m = scan.nextInt();
			fortifications = scan.nextInt();
			scan.nextLine();

			int barbarians = scan.nextInt();
			// Retin nodurile care sunt asezari de barbari.
			for (int i = 0; i < barbarians; i++) {
				int barbarianId = scan.nextInt();
				barbarianNodes[barbarianId] = true;
			}

			for (int i = 0; i < m; i++) {
				scan.nextLine();
				int x = scan.nextInt();
				int y = scan.nextInt();
				int cost = scan.nextInt();
				// Adaug muchia atat de la x la y, cat si de la y la x,
				// intrucat este graf neorientat.
				adj[x].add(new Pair(y, cost));
				adj[y].add(new Pair(x, cost));
			}
		} catch (FileNotFoundException e) {
			System.out.println("Read file not found");
			e.printStackTrace();
		}
	}

	// Functie care scrie rezultatele in fisierul de iesire.
	private static void write() {
		try {
			FileWriter out = new FileWriter("fortificatii.out");
			out.write(result.toString());
			out.close();
		} catch (IOException e) {
			System.out.println("Write file error");
			e.printStackTrace();
		}
	}

	// Functie care foloseste algoritmul lui Dijkstra pentru a calcula toate
	// distantele de la nodul 1 la celelalte noduri.
	private static void getDistances() {
		dist[1] = 0;

		boolean[] visited = new boolean[n + 1];

		// Folosesc o coada de prioritati sortata dupa costul muchiilor.
		PriorityQueue<Pair> queue = new PriorityQueue<>(1, Comparator.comparingLong(p -> p.cost));
		queue.add(new Pair(1, 0));

		while (!queue.isEmpty()) {
			// Extrag primul element.
			Pair current = queue.poll();

			// Daca nodul curent este asezare de barbari, il sar, intrucat nu pot trece prin el.
			if (barbarianNodes[current.node]) {
				continue;
			}

			// Parcurg muchiile nodului curent.
			for (Pair pair : adj[current.node]) {
				// Daca nodul destinatie al muchiei nu este vizitat si pot obtine
				// un drum de costmai mic, actualizez in vectorul de distante si adaug muchia
				// cu costul actualizat in coada.
				if (!visited[pair.node]) {
					if (dist[current.node] + pair.cost < dist[pair.node]) {
						dist[pair.node] = dist[current.node] + pair.cost;
						queue.add(new Pair(pair.node, dist[pair.node]));
					}
				}
			}
			visited[current.node] = true;
		}
	}

	private static void solve() {
		// Calculez distantele de la nodul 1 la restul nodurilor.
		getDistances();

		// Folosesc o cautare binara pentru a cauta timpul maxim pe care il pot obtine
		// folosind numarul de fortificatii disponibile.
		long left = 0;
		long right = Long.MAX_VALUE;

		while (left <= right) {
			// Variabila mij reprezinta timpul pe care il verific daca poate fi obtinut.
			long mij = left + (right - left) / 2;
			// Numarul de fortificatii folosite.
			long fortifUsed = 0;
			// Boolean folosit pentru a decide daca sa trec la urmatoarea iteratie din
			// cautarea binara.
			boolean next = false;
			// Parcurg toate nodurile care reprezinta asezari de barbari.
			for (int i = 1; i <= n; i++) {
				if (!barbarianNodes[i]) {
					continue;
				}
				// Caut toate muchiile intre nodul ales si un nod care apartine imperiului.
				for (Pair pair : adj[i]) {
					if (!barbarianNodes[pair.node]) {
						// Verific daca timpul ales (din care scad timpul pentru a ajunge la
						// asezarea ce apartine imperiului intrucat nu este relevanta si nu poate
						// fi modificata) este mai mare decat costul muchiei, caz in care folosesc
						// fortificatii pentru a-l aduce la timpul ales.
						if (mij - dist[pair.node] > pair.cost) {
							fortifUsed += mij - dist[pair.node] - pair.cost;
							// Verific daca am depasit fortificatiile disponibile, caz in care trec
							// la urmatoarea iteratie a cautarii binare. Folosesc acest if pentru
							// a evita overflow-ul.
							if (fortifUsed > fortifications) {
								next = true;
								break;
							}
						}
					}
				}
				if (next) {
					break;
				}
			}
			// Daca fortificatiile utilizate sunt mai multe decat cele disponibile, inseamna ca
			// trebuie un timp mai mic, altfel actualizez rezultatul si caut daca nu cumva se
			// poate ajunge la un timp mai mare.
			if (fortifUsed > fortifications) {
				right = mij - 1;
			} else {
				result = mij;
				left = mij + 1;
			}
		}
	}

	public static void main(String[] args) {
		read();
		solve();
		write();
	}
}

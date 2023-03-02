import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class Curse {
	static int n;
	static int m;
	static int a;

	// Matricea de antrenamente.
	static short[][] training;

	static List<Integer> results = new ArrayList<>();

	// Matricea de dependente (de adiacenta).
	static boolean[][] dependencies;

	// Vector care retine gradele interioare.
	static short[] inDegrees;
	// Functie care citeste datele din fisierul de intrare.
	private static void read() {
		try {
			File in = new File("curse.in");
			Scanner scan = new Scanner(in);
			n = scan.nextInt();
			m = scan.nextInt();
			a = scan.nextInt();
			scan.nextLine();

			training = new short[a][n];
			dependencies = new boolean[m + 1][m + 1];
			inDegrees = new short[m + 1];
			for (int i = 0; i < a; i++) {
				String[] cars = scan.nextLine().split(" ");
				for (int j = 0; j < n; j++) {
					String car = cars[j];
					training[i][j] = (short) Integer.parseInt(car);
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
			FileWriter out = new FileWriter("curse.out");
			for (Integer car : results) {
				out.write(car.toString() + " ");
			}
			out.close();
		} catch (IOException e) {
			System.out.println("Write file error");
			e.printStackTrace();
		}
	}

	// Functie care verifica daca pentru doua antrenamente consecutive la care difera
	// rezultatul pe o pista, pe toate pistele precedente sa fie egalitate (aceeasi masina).
	private static boolean checkDependency(int i, int j) {
		if (i == 0) {
			return false;
		}
		for (int k = j - 1; k >= 0; k--) {
			if (training[i][k] != training[i - 1][k]) {
				return false;
			}
		}
		return true;
	}

	// Functie care determina dependentele si gradele interioare (o dependenta intre doua masini
	// are loc daca in doua antrenamente consecutive pe aceeasi pista, fiecare masina castiga cate
	// un antrenament, iar (in aceste doua antrenamente) pentru pistele precedente a fost
	// egalitate (a castigat aceeasi masina)).
	private static void getDependencies() {
		for (int j = 0; j < n; j++) {
			for (int i = 1; i < a; i++) {
				if (training[i][j] != training[i - 1][j]) {
					if (checkDependency(i, j)) {
						int better = training[i - 1][j];
						int worse = training[i][j];
						// Actualizez si gradele interioare. Verific daca nu a mai fost gasita deja
						// dependenta, intrucat se pot repeta relatiile obtinute din antrenamente.
						if (!dependencies[better][worse]) {
							inDegrees[worse]++;
						}
						dependencies[better][worse] = true;
					}
				}
			}
		}
	}

	private static void solve() {
		// Obtin dependentele (matricea de adiacenta) si gradele interioare.
		getDependencies();

		// Folosesc o sortare topologica (cu algoritmul lui Kahn) pentru a determina
		// ordinea masinilor.
		boolean[] visited = new boolean[m + 1];
		Queue<Integer> queue = new LinkedList<>();
		// Adaug in coada toate nodurile cu grad interior 0 si le vizitez.
		for (int i = 1; i <= m; i++) {
			if (inDegrees[i] == 0) {
				queue.add(i);
				visited[i] = true;
			}
		}

		while (!queue.isEmpty()) {
			// Extrag primul element.
			int node = queue.poll();
			// Il adaug in rezultate (ordinea in care sunt scoase nodurile reprezinta
			// ordinea finala).
			results.add(node);

			// Sterg arcele care pleaca din nodul extras actualizand gradele interioare.
			for (int i = 1; i <= m; i++) {
				if (dependencies[node][i]) {
					inDegrees[i]--;
				}
			}

			// Adaug in coada nodurile nevizitate cu grad interior 0.
			for (int i = 1; i <= m; i++) {
				if (!visited[i] && inDegrees[i] == 0) {
					queue.add(i);
					visited[i] = true;
				}
			}
		}
	}

	public static void main(String[] args) {
		read();
		solve();
		write();
	}
}
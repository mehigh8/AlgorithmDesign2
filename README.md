<b>Rosu Mihai Cosmin 323CA</b>

<b>Curatare:</b>
- Explicatii:
  - Incep prin a reprezenta fiecare robot si dirty spot drept un nod care are
 in lista sa de adiacenta legatura cu toate celelalte noduri (roboti/dirty
 spot-uri).
  - Apoi, folosind BFS-uri determin distantele intre fiecare doua noduri.
  - Dupa ce am obtinut toate distantele, generez folosind backtracking toate
 alocarile de dirty spot-uri posibile pentru roboti (Fara a tine cont de
 ordinea dirty spot-urilor).
  - Pentru fiecare alocare, determin distanta parcursa de fiecare robot (astfel
 incat sa fie minima) si apoi aleg distanta maxima dintre aceste distante, iar
 aceasta reprezinta rezultatul alocarii.
  - Apoi aleg cel mai mic rezultat.
- Complexitate:
  - Un BFS are O(N * M)
  - Deoarece fac un BFS din fiecare nod => in cel mai rau caz: O(8 * N * M)
  - Backtracking-ul, desi este costisitor, deoarece avem maxim 8 noduri (roboti/
 dirty spot-uri) are complexitatea irelevanta.
  - Complexitate finala: O(N * M)
 
<b>Fortificatii:</b>
- Explicatii:
  - Incep prin a calcula distantele de la nodul 1 (capitala) la toate celelalte
 noduri folosind algoritmul lui Dijkstra.
  - Apoi, folosesc o cautare binara pentru a incerca sa gasesc o valoare de
 timp care sa poate fi obtinuta (sau cat mai aproape) folosind fortificatiile
 disponibile. Daca fortificatiile folosite sunt mai multe decat cele
 disponibile, trebuie sa caut un timp mai mic, altfel retin valoarea (este cea
 mai buna de pana atunci) si caut daca nu cumva exista un timp mai mare care
 se poate realiza cu fortificatiile disponibile.
- Complexitate:
  - Algoritmul lui Dijkstra: O(M * log(N))
  - Cautarea binara are loc de la 0 la 2^63 => O(log(2^63)) = O(63) = O(1)
  - Complexitatea finala: O(M * log(N))

<b>Beamdrone:</b>
- Explicatii:
  - Folosesc algoritmul lui Dijkstra (modificat pentru a respecta regulile
 problemei) pentru a determina drumul minim pana la destinatie. Diferentele
 fata de algoritmul clasic provin din faptul ca nu mai folosesc un vector
 (matrice in cazul asta) de noduri vizitate, intrucat as rata posibile drumuri
 (in unele noduri se poate ajunge pe diferite cai, din directii diferite),
 verific mereu daca distanta retinuta intr-un nod din coada mai este valida
 (daca distanta din matrice este mai mica inseamna ca s-a actualizat intre timp
 si nu mai este valabila cea din coada), si in conditia in care actualizez
 distanta pana la o pozitie, folosesc '<=' in loc de '<' intrucat eu vreau sa
 acopar toate posibilitatile de a ajunge la o pozitie (acelasi motiv, se poate
 ajunge din mai multe directii).
- Complexitate:
  - Algoritmul lui Dijkstra intr-o matrice: O(|V| ^ 2) = O((N * M) ^ 2)
  - Complexitate finala: O((N * M) ^ 2)

<b>Curse:</b>
- Explicatii:
  - Mai intai parcurg matricea de antrenamente pentru a determina relatiile
 (dependintele) intre masini.
  - Dupa ce obtin matricea de adiacenta, folosesc o sortare topologica pentru a
 determina ordinea masinilor.
- Complexitate:
  - Determinarea relatiilor: O(A * N ^ 2)
  - Sortarea topologica: O(M + E), unde E = numarul relatiilor (arcelor)
  - Complexitatea finala: O(A * N ^ 2) + O(M + E)

# CZYTELNICY I PISARZE

Program jest symulacją problemu synchronizacji/wielowątkowości "Czytelnicy i Pisarze".
Celem projektu jest zarządzanie dostępem do współdzielonego zasobu (Biblioteki)
przez wiele wątków (czytelników lub pisarzy) działających jednocześnie.

Kluczowe założenia:
- Dowolna liczba Czytelników może czytać jednocześnie (jeśli nikt nie pisze).
- Tylko jeden Pisarz może pisać w danym momencie.
- Pisarz nie może wejść, jeśli ktoś czyta lub pisze.
- Zastosowano kolejke FIFO, aby zapobiec braku dostępu dla pisarza z uwagi na
  częstą wymianę czytelników - czekający pisarze blokują nowych czytelników.

Wymagania:
----------------------
- Java 21
- Maven 3.8+

Struktura projektu:
----------------------
readers_and_writers
  - Main.java    -> Punkt wejścia, parsowanie argumentów, uruchamianie wątków.
  - Library.java -> Logika biznesowa, monitorowanie, synchronizacja dostępu.
  - Reader.java  -> Wątek czytelnika (czyta: 1-3s, odpoczywa: 1s).
  - Writer.java  -> Wątek pisarza (pisze: 1-3s, odpoczywa: 2s).

src/test/java/readers_and_writers/
  - LibraryTest.java    -> Testy logiki biblioteki i kolejek.
  - ThreadLoopTest.java -> Testy cyklu życia wątków i ich przerywania.
  - MainTest.java       -> Testy argumentów wejściowych aplikacji.

Budowa i kompilacja:
-------------------------
Aby zbudować projekt i pobrać zależności, wykonaj w terminalu komendę:

    mvn clean package

Uruchamianie projektu:
------------------------
Program przyjmuje dwa argumenty opcjonalne:
    Argument 1: Liczba czytelników (Readers)
    Argument 2: Liczba pisarzy (Writers)
    Domyślnie: 10 czytelników, 3 pisarzy.

METODA A: Użycie skryptu pomocniczego
Nadaj plikowi run.sh uprawnienia i uruchom:
    chmod +x run.sh
    ./run.sh              (Uruchamia domyślnie: 10 Readers, 3 Writers)
    ./run.sh 20 5         (Uruchamia: 20 Readers, 5 Writers)

METODA B: Użycie Mavena
    mvn clean compile exec:java -Dexec.mainClass="readers_and_writers.Main" -Dexec.args="{Number of readers} {Number of writers}

METODA C: Uruchomienie z pliku JAR (po zbudowaniu)
    java -cp target/classes readers_and_writers.Main {Number of readers} {Number of writers}

Testy jednostkowe
-------------
Projekt posiada testy jednostkowe, które można uruchomić za pomoćą komendy:
    mvn clean test

Zakończenie działania programu
------------------------
Program działa w pętli nieskończonej symulując ciągłą pracę biblioteki.
Aby zakończyć działanie, użyj skrótu klawiszowego w terminalu:
    Ctrl + C

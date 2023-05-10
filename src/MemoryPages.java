package src;

import javax.management.Query;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class MemoryPages {


    private static int[] pageRequests = new int[] {1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};
    private static int frameQuantity = 4;
    private static int quantityOfPages = 5;
    private static int liczbaOdwolanDoStron = 12;

    public static void main(String[] args) {

//        losoweDane();

        algFIFO();
        algOPT();
        algLRU();
        algAproxLRU();
        algRAND();

    }

    private static void algAproxLRU() {

    }

    private static void losoweDane() {
        Random random = new Random();
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj liczbę stron: ");
        quantityOfPages = sc.nextInt();
        if (quantityOfPages == 0) {
            quantityOfPages = random.nextInt(100);
        }
        System.out.print("Podaj liczbę ramek: ");
        frameQuantity = sc.nextInt();
        if (frameQuantity == 0) {
            frameQuantity = random.nextInt(10);
        }

        System.out.print("Podaj liczbę odwołań do stron: ");
        liczbaOdwolanDoStron = sc.nextInt();
        if (liczbaOdwolanDoStron == 0) {
            liczbaOdwolanDoStron = random.nextInt(1000);
        }

        pageRequests = new int[liczbaOdwolanDoStron];
        for (int i = 0; i < liczbaOdwolanDoStron; i++) {
            pageRequests[i] = random.nextInt(quantityOfPages - 1) + 1;
        }
    }

    private static void algRAND() {
        int pageFaults = 0;
        Set<Integer> pageSet = new HashSet<>();

        for (int i = 0; i < liczbaOdwolanDoStron; i++) {
            int page = pageRequests[i];
            Random random = new Random();

            // if fault
            if (!pageSet.contains(page)) {
                pageFaults++;
                if (pageSet.size() >= frameQuantity) {
                    pageSet.remove(random.nextInt(frameQuantity - 1) + 1);
                }
                pageSet.add(page);
            }
        }
        System.out.println("Metoda RAND - Liczba błędów strony: " + pageFaults);
    }

    private static void algLRU() {
        int pageFaults = 0;
        Set<Integer> pageSet = new HashSet<>();

        for (int i = 0; i < liczbaOdwolanDoStron; i++) {
            int page = pageRequests[i];
            // if fault
            if (!pageSet.contains(page)) {
                pageFaults++;
                if (pageSet.size() >= frameQuantity) {
                    Stack<Integer> leastUsed = new Stack<>();
                    for (int j = i - 1; j >= 0 ; j--) {
                        if (leastUsed.size() == frameQuantity) break;
                        if (!leastUsed.contains(pageRequests[j])) {
                            leastUsed.push(pageRequests[j]);
                        }
                    }
                    pageSet.remove(leastUsed.peek());
                }
                pageSet.add(page);
            }
        }
        System.out.println("Metoda LRU - Liczba błędów strony: " + pageFaults);
    }


    private static void algOPT() {
        int pageFaults = 0;
        Set<Integer> pageSet = new HashSet<>();

        for (int i = 0; i < liczbaOdwolanDoStron; i++) {
            int page = pageRequests[i];
            int removedPage = 0;
            // if fault
            if (!pageSet.contains(page)) {
                pageFaults++;
                if (pageSet.size() >= frameQuantity) {
                    Stack<Integer> willbeUsed = new Stack<>();
                    for (int j = i +1; j <= liczbaOdwolanDoStron - 1; j++) {
                        if (willbeUsed.size() == frameQuantity) {
                            removedPage = willbeUsed.peek();
                            break;
                        }
                        if (!willbeUsed.contains(pageRequests[j])) {
                            willbeUsed.push(pageRequests[j]);
                        }
                    }
                    if (willbeUsed.size() < frameQuantity) {
                        Random random = new Random();
                        while (willbeUsed.contains(removedPage)) {
                            removedPage = random.nextInt(quantityOfPages - 1) + 1;
                        }
                    }
                    pageSet.remove(removedPage);
                }
                pageSet.add(page);
            }
        }
        System.out.println("Metoda OPT - Liczba błędów strony: " + pageFaults);
    }

    private static void algFIFO() {
        int pageFaults = 0;
        Queue<Integer> pageQueue = new LinkedList<>();
        Set<Integer> pageSet = new HashSet<>();

        for (int i = 0; i < liczbaOdwolanDoStron; i++) {
            int page = pageRequests[i];
            if (!pageSet.contains(page)) {
                pageFaults++;
                if (pageQueue.size() == frameQuantity) {
                    pageSet.remove(pageQueue.remove());
                }
                pageQueue.add(page);
                pageSet.add(page);
            }
        }
        System.out.println("Metoda FIFO - Liczba błędów strony: " + pageFaults);
    }
}

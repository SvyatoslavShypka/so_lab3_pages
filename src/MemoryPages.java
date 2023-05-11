import java.util.*;

public class MemoryPages {

//   data by default
    private static int[] pageRequests = new int[] {1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5};
    private static int frameQuantity = 4;
    private static int quantityOfPages = 5;
    private static int liczbaOdwolanDoStron = 12;

    public static void main(String[] args) {

        losoweDane();
        System.out.print("pageRequests = ");
        for (int i = 0; i < pageRequests.length; i++) {
            System.out.print(pageRequests[i] + ", ");
        }
        System.out.println();
        System.out.println("frameQuantity = " + frameQuantity);
        System.out.println("quantityOfPages = " + quantityOfPages);
        System.out.println("liczbaOdwolanDoStron = " + liczbaOdwolanDoStron);

        algFIFO();
        algOPT();
        algLRU();
        algAproxLRU();
        algRAND();

    }

    private static void algAproxLRU() {
        int pageFaults = 0;
        Set<Integer> pageSet = new HashSet<>();
        List<Integer> pagesRecentlyCalledList = new ArrayList<>();
        int[] bitArray = new int[quantityOfPages + 1];
        for (int i = 0; i < bitArray.length; i++) {
            // 0 - means that page was not called recently
            bitArray[i] = 0;
        }

        for (int i = 0; i < liczbaOdwolanDoStron; i++) {
            int page = pageRequests[i];
            if (pagesRecentlyCalledList.size() > frameQuantity + 1) {
                pagesRecentlyCalledList.remove(0);
            }
            bitArray[page] = 1;
            // if fault
            if (!pageSet.contains(page)) {
                pageFaults++;
                if (pageSet.size() >= frameQuantity) {
                    int removePage = 0;
                    for (int j = 0; j < pagesRecentlyCalledList.size(); j++) {
                        removePage = pagesRecentlyCalledList.get(j);
                        if (bitArray[removePage] == 0) {
                            break;
                        }
                        if (j == pagesRecentlyCalledList.size() -1) {
                            for (int k = 0; k < bitArray.length; k++) {
                                // 0 - means that page was not called recently
                                bitArray[k] = 0;
                            }
                            removePage = pagesRecentlyCalledList.get(0);
                        }
                    }
                    pageSet.remove(removePage);
                    pagesRecentlyCalledList.remove(0);
                }
                pageSet.add(page);
                pagesRecentlyCalledList.add(page);
                bitArray[page] = 1;
            }
        }
        System.out.println("Metoda AproxLRU - Liczba błędów strony: " + pageFaults);
    }

    private static void losoweDane() {
        Random random = new Random();
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj liczbę ramek: ");
        frameQuantity = sc.nextInt();
        if (frameQuantity == 0) {
            frameQuantity = random.nextInt(10) + 4;
        }

        System.out.print("Podaj liczbę stron: ");
        quantityOfPages = sc.nextInt();
        if (quantityOfPages == 0) {
            quantityOfPages = random.nextInt(100) + frameQuantity + 1;
        }
        System.out.print("Podaj liczbę odwołań do stron: ");
        liczbaOdwolanDoStron = sc.nextInt();
        if (liczbaOdwolanDoStron == 0) {
            liczbaOdwolanDoStron = random.nextInt(2000) + 1000;
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
                    int randomPage = getRandomPage(pageSet, random);
//                    pageSet.remove(random.nextInt(frameQuantity - 1) + 1);
                    pageSet.remove(randomPage);
                }
                pageSet.add(page);
            }
        }
        System.out.println("Metoda RAND - Liczba błędów strony: " + pageFaults);
    }

    private static int getRandomPage(Set<Integer> pageSet, Random random) {
        int randomIndex = random.nextInt(pageSet.size());
        int i = 0;
        for(int page : pageSet) {
            if (i == randomIndex) {
                return page;
            }
            i++;
        }
        throw new IllegalStateException("Empty page set");
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

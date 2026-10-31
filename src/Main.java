import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        task1();
        System.out.println();
        task2();
        System.out.println();
        task3();
    }


    public static void task1() {
        System.out.println("Задание 1. Биты доступа");
        printAccessLevelExample(new int[]{3, 0, 2, 3, 1});
        printAccessLevelExample(new int[]{2, 2, 0, 3});
        System.out.println("Сложность: O(n + k) по времени и памяти, k = 4, то есть O(n).");
    }


    public static void task2() {
        System.out.println("Задание 2. Коды заказов");
        printOrderCodesExample(new int[]{314159, 271828, 123456});
        printOrderCodesExample(new int[]{90, 9, 900});
        System.out.println("Сложность: O(d * (n + 10)) по времени, O(n + 10) по памяти.");
        System.out.println("d — число разрядов; при фиксированном d время и память — O(n).");
    }

    public static void task3() {
        System.out.println("Задание 3. Доли голосов");
        printVoteSharesExample(new double[]{0.25, 0.70, 0.10, 0.90});
        printVoteSharesExample(new double[]{0.05, 0.05, 0.80});
        System.out.println("Сложность: ожидаемое время O(n) при равномерном распределении,");
        System.out.println("в худшем случае O(n^2); дополнительная память O(n).");
    }

    public record User(String name, int accessLevel) {
    }

    public static User[] sortUsersByAccessLevel(User[] users) {
        int[] count = new int[4];

        for (User user : users) {
            int level = user.accessLevel();
            if (level < 0 || level >= count.length) {
                throw new IllegalArgumentException("Уровень доступа должен быть от 0 до 3");
            }
            count[level]++;
        }


        for (int level = 1; level < count.length; level++) {
            count[level] += count[level - 1];
        }

        User[] sorted = new User[users.length];

        for (int i = users.length - 1; i >= 0; i--) {
            int level = users[i].accessLevel();
            sorted[--count[level]] = users[i];
        }

        return sorted;
    }

    public static int[] sortOrderCodes(int[] codes) {
        int max = 0;
        for (int code : codes) {
            if (code < 0) {
                throw new IllegalArgumentException("Код заказа должен быть неотрицательным");
            }
            max = Math.max(max, code);
        }

        int[] sorted = codes.clone();
        // long предотвращает переполнение при переходе к следующему разряду.
        for (long place = 1; max / place > 0; place *= 10) {
            sortByDigit(sorted, place);
        }
        return sorted;
    }

    private static void sortByDigit(int[] codes, long place) {
        int[] count = new int[10];
        for (int code : codes) {
            int digit = (int) (code / place % 10);
            count[digit]++;
        }

        for (int digit = 1; digit < count.length; digit++) {
            count[digit] += count[digit - 1];
        }

        int[] sorted = new int[codes.length];
        // Устойчивый проход сохраняет порядок, полученный по младшим разрядам.
        for (int i = codes.length - 1; i >= 0; i--) {
            int digit = (int) (codes[i] / place % 10);
            sorted[--count[digit]] = codes[i];
        }
        System.arraycopy(sorted, 0, codes, 0, codes.length);
    }

    public static double[] sortVoteShares(double[] shares) {
        int bucketCount = shares.length;
        if (bucketCount == 0) {
            return new double[0];
        }

        List<List<Double>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }

        for (double share : shares) {
            if (!Double.isFinite(share) || share < 0 || share > 1) {
                throw new IllegalArgumentException("Доля голосов должна быть числом от 0 до 1");
            }
            // Значение 1 включаем в последнюю корзину.
            int index = Math.min((int) (share * bucketCount), bucketCount - 1);
            buckets.get(index).add(share);
        }

        double[] sorted = new double[shares.length];
        int index = 0;
        for (List<Double> bucket : buckets) {
            sortBucket(bucket);
            for (double share : bucket) {
                sorted[index++] = share;
            }
        }
        return sorted;
    }

    private static void sortBucket(List<Double> bucket) {
        // Сортировка вставками внутри одной корзины.
        for (int i = 1; i < bucket.size(); i++) {
            double value = bucket.get(i);
            int j = i - 1;
            while (j >= 0 && Double.compare(bucket.get(j), value) > 0) {
                bucket.set(j + 1, bucket.get(j));
                j--;
            }
            bucket.set(j + 1, value);
        }
    }

    private static void printVoteSharesExample(double[] shares) {
        System.out.println("Вход: " + Arrays.toString(shares));
        System.out.println("Результат: " + Arrays.toString(sortVoteShares(shares)));
        System.out.println();
    }

    private static void printOrderCodesExample(int[] codes) {
        System.out.println("Вход: " + Arrays.toString(codes));
        System.out.println("Результат: " + Arrays.toString(sortOrderCodes(codes)));
        System.out.println();
    }

    private static void printAccessLevelExample(int[] levels) {
        User[] users = new User[levels.length];
        for (int i = 0; i < levels.length; i++) {
            users[i] = new User("Пользователь " + (i + 1), levels[i]);
        }

        User[] sorted = sortUsersByAccessLevel(users);
        int[] sortedLevels = new int[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            sortedLevels[i] = sorted[i].accessLevel();
        }

        System.out.println("Вход: " + Arrays.toString(levels));
        System.out.println("Результат: " + Arrays.toString(sortedLevels));
        System.out.println("Порядок пользователей: " + Arrays.toString(sorted));
        System.out.println();
    }
}

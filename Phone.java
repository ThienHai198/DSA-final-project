import java.util.*;
import javax.swing.*;


public class Phone {
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> phoneList = new ArrayList<>();

    public void addName(String n, String p) {
        nameList.add(n);
        phoneList.add(p);
        quickSort(0, nameList.size() - 1);
    }

    public void delete(String s) {
        int index = binarySearch(s);
        if (index != -1) {
            nameList.remove(index);
            phoneList.remove(index);
        }
    }

    int binarySearch(String key) {
        int low = 0, high = nameList.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int compare = nameList.get(mid).compareTo(key);
            if (compare == 0) {
                return mid;
            } else if (compare < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        String pivot = nameList.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (nameList.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(nameList, i, j);
                Collections.swap(phoneList, i, j);
            }
        }
        Collections.swap(nameList, i + 1, high);
        Collections.swap(phoneList, i + 1, high);
        return i + 1;
    }


        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Phone phoneBook = new Phone();

                    // Expanded list of first and last names
                    String[] firstNames = {
                            "John", "Jane", "Alex", "Chris", "Taylor", "Jordan", "Morgan", "Casey", "Jamie", "Cameron",
                            "Abby", "Adam", "Avery", "Bella", "Ben", "Brooke", "Caleb", "Chloe", "Daniel", "Daisy",
                            "Dylan", "Ella", "Ethan", "Eva", "Finn", "Grace", "Harry", "Holly", "Isaac", "Ivy",
                            "Jack", "Lily", "Liam", "Lucy", "Mason", "Mia", "Noah", "Olivia", "Owen", "Paige",
                            "Peter", "Riley", "Ruby", "Ryan", "Sophia", "Samuel", "Stella", "Theo", "Zoe"
                    };

                    String[] lastNames = {
                            "Smith", "Johnson", "Brown", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
                            "Allen", "Baker", "Bell", "Campbell", "Clark", "Collins", "Davis", "Edwards", "Evans", "Foster",
                            "Garcia", "Green", "Hall", "Harris", "Hughes", "King", "Lee", "Lewis", "Lopez", "Martin",
                            "Mitchell", "Moore", "Murphy", "Nelson", "Parker", "Perez", "Peterson", "Phillips", "Powell",
                            "Reed", "Rogers", "Scott", "Simpson", "Stewart", "Taylor", "Thomas", "Walker", "White", "Wilson", "Young"
                    };

                    Random random = new Random();

                    // Set to store unique names and phone numbers
                    Set<String> namesSet = new HashSet<>();
                    Set<String> phoneNumbersSet = new HashSet<>();

                    // Generate 500 unique names and phone numbers
                    while (namesSet.size() < 500) {
                        String firstName = firstNames[random.nextInt(firstNames.length)];
                        String lastName = lastNames[random.nextInt(lastNames.length)];
                        String name = firstName + " " + lastName;

                        // Generate a random phone number
                        String phone = String.format("%04d-%04d", random.nextInt(9000) + 1000, random.nextInt(9000) + 1000);

                        // Ensure the name and phone are unique
                        if (!namesSet.contains(name) && !phoneNumbersSet.contains(phone)) {
                            namesSet.add(name);
                            phoneNumbersSet.add(phone);
                            phoneBook.addName(name, phone); // Assuming this method stores the name and phone number
                        }
                    }

                    // Set up the GUI (assuming it uses phoneBook)
                    GUI gui = new GUI();
                    gui.phoneBook = phoneBook;
                }
            });
        }




}
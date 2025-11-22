import java.util.*;
import java.io.*;

class Student implements Serializable {
    private int id;
    private String name;
    private int age;
    private String course;
    private double gpa;
    
    public Student(int id, String name, int age, String course, double gpa) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.gpa = gpa;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public double getGpa() { return gpa; }
    
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    
    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Age: %d | Course: %s | GPA: %.2f", 
                           id, name, age, course, gpa);
    }
}

class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}

class StudentManagementSystem {
    private ArrayList<Student> students;
    private static final String FILE_NAME = "students.dat";
    
    public StudentManagementSystem() {
        students = new ArrayList<>();
        loadStudents();
    }
    
    public void addStudent(Student student) {
        students.add(student);
        System.out.println("✓ Student added successfully!");
        saveStudents();
    }
    
    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students in the system.");
            return;
        }
        System.out.println("\n========== ALL STUDENTS ==========");
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println("==================================\n");
    }
    
    public Student searchStudentById(int id) throws StudentNotFoundException {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new StudentNotFoundException("Student with ID " + id + " not found!");
    }
    
    public void updateStudent(int id, String name, int age, String course, double gpa) 
            throws StudentNotFoundException {
        Student student = searchStudentById(id);
        student.setName(name);
        student.setAge(age);
        student.setCourse(course);
        student.setGpa(gpa);
        System.out.println("✓ Student updated successfully!");
        saveStudents();
    }
    
    public void deleteStudent(int id) throws StudentNotFoundException {
        Student student = searchStudentById(id);
        students.remove(student);
        System.out.println("✓ Student deleted successfully!");
        saveStudents();
    }
    
    public void sortStudentsByGPA() {
        Collections.sort(students, (s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()));
        System.out.println("✓ Students sorted by GPA (highest to lowest)");
    }
    
    public void calculateAverageGPA() {
        if (students.isEmpty()) {
            System.out.println("No students to calculate average.");
            return;
        }
        double sum = 0;
        for (Student s : students) {
            sum += s.getGpa();
        }
        double avg = sum / students.size();
        System.out.printf("Average GPA: %.2f\n", avg);
    }
    
    private void saveStudents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadStudents() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                students = (ArrayList<Student>) ois.readObject();
                System.out.println("✓ Data loaded successfully!");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
        }
    }
}

public class StudentManagementApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManagementSystem sms = new StudentManagementSystem();
        
        while (true) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║  STUDENT MANAGEMENT SYSTEM         ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Sort by GPA");
            System.out.println("7. Calculate Average GPA");
            System.out.println("8. Exit");
            System.out.print("\nEnter your choice: ");
            
            int choice = sc.nextInt();
            sc.nextLine();
            
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Enter ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter Age: ");
                        int age = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Course: ");
                        String course = sc.nextLine();
                        System.out.print("Enter GPA: ");
                        double gpa = sc.nextDouble();
                        
                        Student student = new Student(id, name, age, course, gpa);
                        sms.addStudent(student);
                        break;
                        
                    case 2:
                        sms.displayAllStudents();
                        break;
                        
                    case 3:
                        System.out.print("Enter Student ID: ");
                        int searchId = sc.nextInt();
                        Student found = sms.searchStudentById(searchId);
                        System.out.println("\n" + found);
                        break;
                        
                    case 4:
                        System.out.print("Enter Student ID to update: ");
                        int updateId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter New Name: ");
                        String newName = sc.nextLine();
                        System.out.print("Enter New Age: ");
                        int newAge = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter New Course: ");
                        String newCourse = sc.nextLine();
                        System.out.print("Enter New GPA: ");
                        double newGpa = sc.nextDouble();
                        
                        sms.updateStudent(updateId, newName, newAge, newCourse, newGpa);
                        break;
                        
                    case 5:
                        System.out.print("Enter Student ID to delete: ");
                        int deleteId = sc.nextInt();
                        sms.deleteStudent(deleteId);
                        break;
                        
                    case 6:
                        sms.sortStudentsByGPA();
                        sms.displayAllStudents();
                        break;
                        
                    case 7:
                        sms.calculateAverageGPA();
                        break;
                        
                    case 8:
                        System.out.println("Thank you for using the system!");
                        sc.close();
                        System.exit(0);
                        
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (StudentNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input type!");
                sc.nextLine();
            }
        }
    }
}

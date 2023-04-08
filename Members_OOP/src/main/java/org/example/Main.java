// Ahmet Kemal Hacioglu_19120205050 2023 BIL122 OOP Midterm Project
//This code gets name, surname and email of a new member from user and write them into a specific text file according to their group.
//Also, this code gets data from specific text files and sends email to members in it.
//Important Note: For sending mail operation to work, you should allow your email to getting mail from less secure third-party applications

package org.example;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;

class Member {
    protected String name;
    protected String surname;
    protected String email;

    public Member(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}

class EliteMember extends Member {
    public EliteMember(String name, String surname, String email) {
        super(name, surname, email);
    }

    public void saveToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("EliteMembers.txt", true));
        writer.write(name + "\t" + surname + "\t" + email + "\n");
        writer.close();
    }
}

class CommonMember extends Member {
    public CommonMember(String name, String surname, String email) {
        super(name, surname, email);
    }

    public void saveToFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("CommonMembers.txt", true));
        writer.write(name + "\t" + surname + "\t" + email + "\n");
        writer.close();
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("1- Add an Elite Member");
            System.out.println("2- Add a Common Member");
            System.out.println("3- Send a Mail");
            System.out.println("0- Exit");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Name: ");
                    String eliteName = scanner.nextLine();
                    System.out.print("Surname: ");
                    String eliteSurname = scanner.nextLine();
                    System.out.print("Email: ");
                    String eliteEmail = scanner.nextLine();
                    EliteMember eliteMember = new EliteMember(eliteName, eliteSurname, eliteEmail);
                    try {
                        eliteMember.saveToFile();
                        System.out.println("Elite member added successfully.");
                    } catch (IOException e) {
                        System.err.println("Error while saving elite member information to file.");
                    }
                    break;
                case 2:
                    System.out.print("Name: ");
                    String commonName = scanner.nextLine();
                    System.out.print("Surname: ");
                    String commonSurname = scanner.nextLine();
                    System.out.print("Email: ");
                    String commonEmail = scanner.nextLine();
                    CommonMember commonMember = new CommonMember(commonName, commonSurname, commonEmail);
                    try {
                        commonMember.saveToFile();
                        System.out.println("Common member added successfully.");
                    } catch (IOException e) {
                        System.err.println("Error while saving common member information to file.");
                    }
                    break;
                case 3:
                    int mailChoice;
                    do {
                        System.out.println("1- Mail to every Elite Members");
                        System.out.println("2- Mail to every Common Members");
                        System.out.println("3- Mail to every both Elite and Common Members");
                        System.out.println("0- Back to main menu");
                        mailChoice = scanner.nextInt();
                        scanner.nextLine(); // consume the newline character

                        switch (mailChoice) {
                            case 1:
                                System.out.print("Enter the message: ");
                                String eliteMessage = scanner.nextLine();
                                try {
                                    ArrayList<String> eliteEmails = getEmailsFromFile("EliteMembers.txt");
                                    sendEmails(eliteEmails, "Elite Members", eliteMessage);
                                    System.out.println("Mail sent to elite members successfully.");
                                } catch (IOException e) {
                                    System.err.println("Error while reading elite member information from file.");
                                } catch (MessagingException e) {
                                    System.err.println("Error while sending mail to elite members.");
                                }
                                break;
                            case 2:
                                System.out.print("Enter the message: ");
                                String commonMessage = scanner.nextLine();
                                try {
                                    ArrayList<String> commonEmails = getEmailsFromFile("CommonMembers.txt");
                                    sendEmails(commonEmails, "Common Members", commonMessage);
                                    System.out.println("Mail sent to common members successfully.");
                                } catch (IOException e) {
                                    System.err.println("Error while reading common member information from file.");
                                } catch (MessagingException e) {
                                    System.err.println("Error while sending mail to common members.");
                                }
                                break;
                            case 3:
                                System.out.print("Enter the message: ");
                                String bothMessage = scanner.nextLine();
                                try {
                                    ArrayList<String> eliteEmails = getEmailsFromFile("EliteMembers.txt");
                                    ArrayList<String> commonEmails = getEmailsFromFile("CommonMembers.txt");
                                    eliteEmails.addAll(commonEmails);
                                    sendEmails(eliteEmails, "All Members", bothMessage);
                                    System.out.println("Mail sent to all members successfully.");
                                } catch (IOException e) {
                                    System.err.println("Error while reading member information from file.");
                                } catch (MessagingException e) {
                                    System.err.println("Error while sending mail to all members.");
                                }
                                break;
                            case 0:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                        }
                    } while (mailChoice != 0);
                    break;
                case 0:
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private static ArrayList<String> getEmailsFromFile(String fileName) throws IOException {
        ArrayList<String> emails = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            return emails;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            if (parts.length == 3) {
                String email = parts[2];
                emails.add(email);
            }
        }
        reader.close();
        return emails;
    }

    private static void sendEmails(ArrayList<String> emails, String subject, String message) throws MessagingException {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.office365.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("example@outlook.com", "password");
            }
        };
        Session session = Session.getInstance(properties, auth);
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress("example@outlook.com"));
        for (String email : emails) {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        }
        mimeMessage.setSubject(subject);
        mimeMessage.setText(message);
        Transport.send(mimeMessage);
    }
}



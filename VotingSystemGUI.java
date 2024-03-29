package voting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VotingSystemGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextField ageField;
    private JTextField voterIdField;

    public VotingSystemGUI() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel();
        JPanel partyPanel = createPartyPanel();

        cardPanel.add(loginPanel, "login");
        cardPanel.add(partyPanel, "party");

        add(cardPanel);

        setSize(600, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        ageField = new JTextField();
        voterIdField = new JTextField();
        JButton loginButton = new JButton("Login");
        JButton adminButton = new JButton("Admin Panel");

        inputPanel.add(new JLabel("Enter your Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Enter your Voter ID:"));
        inputPanel.add(voterIdField);

        buttonPanel.add(loginButton);
        buttonPanel.add(adminButton);

        loginPanel.add(inputPanel, BorderLayout.CENTER);
        loginPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String age = ageField.getText();
                String voterId = voterIdField.getText();

                // Navigate to Party Selection page after successful login
                cardLayout.show(cardPanel, "party");
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open Admin Panel
                new Admin().setVisible(true);
            }
        });

        return loginPanel;
    }

    private JPanel createPartyPanel() {
        JPanel partyPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));

        JButton partyButton1 = new JButton("Bharatiya Janata Party (BJP)");
        JButton partyButton2 = new JButton("Indian National Congress (INC)");
        JButton partyButton3 = new JButton("Aam Aadmi Party (AAP)");
        JButton notaButton = new JButton("NOTA");

        buttonPanel.add(partyButton1);
        buttonPanel.add(partyButton2);
        buttonPanel.add(partyButton3);
        buttonPanel.add(notaButton);

        partyPanel.add(buttonPanel, BorderLayout.CENTER);

        partyButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("BJP");
            }
        });

        partyButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("INC");
            }
        });

        partyButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("AAP");
            }
        });

        notaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerVote("NOTA");
            }
        });

        return partyPanel;
    }

    private void registerVote(String party) {
        String age = ageField.getText();
        String voterId = voterIdField.getText();
        String hashedAge = hashString(age);
        String hashedVoterId = hashString(voterId);

        VotingSystemDAO dao = new VotingSystemDAO();
        dao.insertVoterDetails(hashedAge, hashedVoterId, party);
        dao.closeConnection();

        // Navigate back to login page after registering vote
        cardLayout.show(cardPanel, "login");
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VotingSystemGUI();
            }
        });
    }
}

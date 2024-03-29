package voting;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Admin extends JFrame {

    private JPasswordField passwordField;
    private JButton enterButton;

    public Admin() {
        setTitle("Admin Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new GridLayout(2, 2));
        passwordField = new JPasswordField();
        enterButton = new JButton("Enter");

        loginPanel.add(new JLabel("Enter Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty label for spacing
        loginPanel.add(enterButton);

        add(loginPanel);

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passwordField.getPassword());

                // Check if the provided password matches the expected password
                if ("hehe".equals(password)) {
                    // If authenticated, show the admin panel
                    initializeAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(Admin.this, "Invalid password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void initializeAdminPanel() {
        // Create and show voting results window
        VotingResultsWindow resultsWindow = new VotingResultsWindow();
        resultsWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Admin().setVisible(true);
            }
        });
    }
}

class VotingResultsWindow extends JFrame {

    private JTable voterDetailsTable;
    private JTextArea resultTextArea;

    public VotingResultsWindow() {
        setTitle("Voting Results");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panel for voter details table
        JPanel voterDetailsPanel = new JPanel(new BorderLayout());
        voterDetailsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(voterDetailsTable);
        voterDetailsPanel.add(scrollPane, BorderLayout.CENTER);

        // Create panel for result text area
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

        // Create container panel to hold both voter details and result panels
        JPanel containerPanel = new JPanel(new GridLayout(1, 2));
        containerPanel.add(voterDetailsPanel);
        containerPanel.add(resultPanel);

        add(containerPanel);

        // Fetch voting details and display them
        displayVoterDetails();
        displayVotingResults();
    }

    private void displayVoterDetails() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Hashed Voter ID");
        model.addColumn("Party Voted For");

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_system", "root", "hehe");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT hashed_voter_id, party_voted_for FROM voters");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String hashedVoterId = resultSet.getString("hashed_voter_id");
                String partyVotedFor = resultSet.getString("party_voted_for");
                model.addRow(new Object[]{hashedVoterId, partyVotedFor});
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        voterDetailsTable.setModel(model);
    }

    private void displayVotingResults() {
        StringBuilder result = new StringBuilder();
        int bjpVotes = 0;
        int congressVotes = 0;
        int aapVotes = 0;
        int notaVotes = 0;

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/voting_system", "root", "hehe");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT party_voted_for, COUNT(*) AS vote_count FROM voters GROUP BY party_voted_for");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String partyVotedFor = resultSet.getString("party_voted_for");
                int voteCount = resultSet.getInt("vote_count");

                // Increment vote count for each party
                switch (partyVotedFor) {
                    case "BJP":
                        bjpVotes += voteCount;
                        break;
                    case "INC":
                        congressVotes += voteCount; // "INC" stands for Indian National Congress
                        break;
                    case "AAP":
                        aapVotes += voteCount;
                        break;
                    case "NOTA":
                        notaVotes += voteCount;
                        break;
                }
            }

            // Display total votes for each party
            result.append("Party: BJP, Votes: ").append(bjpVotes).append("\n");
            result.append("Party: INC, Votes:").append(congressVotes).append("\n"); // Display total votes for INC
            result.append("Party: AAP, Votes: ").append(aapVotes).append("\n"); // Display total votes for AAP
            result.append("Party: NOTA, Votes: ").append(notaVotes).append("\n"); // Display total votes for NOTA

            resultTextArea.setText(result.toString());

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

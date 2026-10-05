import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Courier & Parcel Tracking System");

        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 15, 15));

        JLabel title = new JLabel(
                "COURIER & PARCEL TRACKING SYSTEM",
                SwingConstants.CENTER
        );

        JButton addCustomerButton = new JButton("Add Customer");
        JButton bookParcelButton = new JButton("Book Parcel");
        JButton trackParcelButton = new JButton("Track Parcel");
        JButton updateStatusButton = new JButton("Update Status");
        JButton viewParcelsButton = new JButton("View Parcels");
        JButton exitButton = new JButton("Exit");


        // ================= ADD CUSTOMER =================

        addCustomerButton.addActionListener(e -> {

            JTextField nameField = new JTextField();
            JTextField mobileField = new JTextField();
            JTextField addressField = new JTextField();

            Object[] fields = {
                    "Name:", nameField,
                    "Mobile:", mobileField,
                    "Address:", addressField
            };

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    fields,
                    "Add Customer",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {

                String name = nameField.getText();
                String mobile = mobileField.getText();
                String address = addressField.getText();

                if (name.isEmpty() || mobile.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Name and mobile are required!"
                    );
                    return;
                }

                try {

                    Connection connection =
                            DatabaseConnection.getConnection();

                    String sql =
                            "INSERT INTO customers " +
                                    "(name, mobile, address) VALUES (?, ?, ?)";

                    PreparedStatement statement =
                            connection.prepareStatement(sql);

                    statement.setString(1, name);
                    statement.setString(2, mobile);
                    statement.setString(3, address);

                    statement.executeUpdate();

                    statement.close();
                    connection.close();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Customer added successfully!"
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error: " + ex.getMessage()
                    );
                }
            }
        });


        // ================= BOOK PARCEL =================

        bookParcelButton.addActionListener(e -> {

            JTextField senderIdField = new JTextField();
            JTextField receiverField = new JTextField();
            JTextField sourceField = new JTextField();
            JTextField destinationField = new JTextField();
            JTextField parcelTypeField = new JTextField();

            Object[] fields = {
                    "Sender Customer ID:", senderIdField,
                    "Receiver Name:", receiverField,
                    "Source:", sourceField,
                    "Destination:", destinationField,
                    "Parcel Type:", parcelTypeField
            };

            int result = JOptionPane.showConfirmDialog(
                    frame,
                    fields,
                    "Book Parcel",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {

                try {

                    int senderId =
                            Integer.parseInt(senderIdField.getText());

                    String receiver =
                            receiverField.getText();

                    String source =
                            sourceField.getText();

                    String destination =
                            destinationField.getText();

                    String parcelType =
                            parcelTypeField.getText();

                    if (receiver.isEmpty()
                            || source.isEmpty()
                            || destination.isEmpty()
                            || parcelType.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                frame,
                                "Please fill all fields!"
                        );

                        return;
                    }

                    Connection connection =
                            DatabaseConnection.getConnection();

                    String parcelSql =
                            "INSERT INTO parcels " +
                                    "(sender_id, receiver_name, source, destination, parcel_type, status) " +
                                    "VALUES (?, ?, ?, ?, ?, 'Booked')";

                    PreparedStatement parcelStatement =
                            connection.prepareStatement(
                                    parcelSql,
                                    Statement.RETURN_GENERATED_KEYS
                            );

                    parcelStatement.setInt(1, senderId);
                    parcelStatement.setString(2, receiver);
                    parcelStatement.setString(3, source);
                    parcelStatement.setString(4, destination);
                    parcelStatement.setString(5, parcelType);

                    parcelStatement.executeUpdate();

                    ResultSet generatedKeys =
                            parcelStatement.getGeneratedKeys();

                    int parcelId = 0;

                    if (generatedKeys.next()) {
                        parcelId = generatedKeys.getInt(1);
                    }

                    String trackingSql =
                            "INSERT INTO tracking " +
                                    "(parcel_id, status, location) " +
                                    "VALUES (?, 'Booked', ?)";

                    PreparedStatement trackingStatement =
                            connection.prepareStatement(trackingSql);

                    trackingStatement.setInt(1, parcelId);
                    trackingStatement.setString(2, source);

                    trackingStatement.executeUpdate();

                    trackingStatement.close();
                    parcelStatement.close();
                    connection.close();

                    JOptionPane.showMessageDialog(
                            frame,
                            "Parcel booked successfully!\n\n" +
                                    "Tracking ID: " + parcelId
                    );

                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Sender Customer ID must be a number!"
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Error: " + ex.getMessage()
                    );
                }
            }
        });


        // ================= TRACK PARCEL =================
// ================= TRACK PARCEL =================

        trackParcelButton.addActionListener(e -> {

            String trackingIdText = JOptionPane.showInputDialog(
                    frame,
                    "Enter Tracking ID:",
                    "Track Parcel",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (trackingIdText == null || trackingIdText.isEmpty()) {
                return;
            }

            try {

                int trackingId =
                        Integer.parseInt(trackingIdText);

                Connection connection =
                        DatabaseConnection.getConnection();

                // Get parcel details
                String parcelSql =
                        "SELECT p.parcel_id, " +
                                "c.name AS sender_name, " +
                                "p.receiver_name, " +
                                "p.source, " +
                                "p.destination, " +
                                "p.parcel_type, " +
                                "p.booking_date " +
                                "FROM parcels p " +
                                "JOIN customers c ON p.sender_id = c.customer_id " +
                                "WHERE p.parcel_id = ?";

                PreparedStatement parcelStatement =
                        connection.prepareStatement(parcelSql);

                parcelStatement.setInt(1, trackingId);

                ResultSet parcelResult =
                        parcelStatement.executeQuery();

                if (!parcelResult.next()) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "No parcel found with Tracking ID: "
                                    + trackingId
                    );

                    parcelResult.close();
                    parcelStatement.close();
                    connection.close();

                    return;
                }

                StringBuilder details =
                        new StringBuilder();

                details.append("TRACKING ID: ")
                        .append(parcelResult.getInt("parcel_id"))
                        .append("\n\n");

                details.append("Sender: ")
                        .append(parcelResult.getString("sender_name"))
                        .append("\n");

                details.append("Receiver: ")
                        .append(parcelResult.getString("receiver_name"))
                        .append("\n");

                details.append("Source: ")
                        .append(parcelResult.getString("source"))
                        .append("\n");

                details.append("Destination: ")
                        .append(parcelResult.getString("destination"))
                        .append("\n");

                details.append("Parcel Type: ")
                        .append(parcelResult.getString("parcel_type"))
                        .append("\n");

                details.append("Booking Date: ")
                        .append(parcelResult.getDate("booking_date"))
                        .append("\n\n");

                details.append("TRACKING HISTORY")
                        .append("\n");

                details.append("--------------------------------")
                        .append("\n");

                parcelResult.close();
                parcelStatement.close();

                // Get complete tracking history
                String trackingSql =
                        "SELECT status, location, updated_at " +
                                "FROM tracking " +
                                "WHERE parcel_id = ? " +
                                "ORDER BY updated_at ASC";

                PreparedStatement trackingStatement =
                        connection.prepareStatement(trackingSql);

                trackingStatement.setInt(1, trackingId);

                ResultSet trackingResult =
                        trackingStatement.executeQuery();

                while (trackingResult.next()) {

                    details.append("Status: ")
                            .append(trackingResult.getString("status"))
                            .append("\n");

                    details.append("Location: ")
                            .append(trackingResult.getString("location"))
                            .append("\n");

                    details.append("Updated: ")
                            .append(trackingResult.getTimestamp("updated_at"))
                            .append("\n");

                    details.append("--------------------------------")
                            .append("\n");
                }

                trackingResult.close();
                trackingStatement.close();
                connection.close();

                JTextArea textArea =
                        new JTextArea(details.toString());

                textArea.setEditable(false);

                textArea.setFont(
                        new Font("Arial", Font.PLAIN, 14)
                );

                JScrollPane scrollPane =
                        new JScrollPane(textArea);

                scrollPane.setPreferredSize(
                        new Dimension(450, 400)
                );

                JOptionPane.showMessageDialog(
                        frame,
                        scrollPane,
                        "Parcel Tracking Details",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Tracking ID must be a number!"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Error: " + ex.getMessage()
                );
            }
        });
                  // ================= UPDATE STATUS =================

        updateStatusButton.addActionListener(e -> {

            String parcelIdText = JOptionPane.showInputDialog(
                    frame,
                    "Enter Parcel/Tracking ID:",
                    "Update Status",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (parcelIdText == null || parcelIdText.isEmpty()) {
                return;
            }

            try {

                int parcelId =
                        Integer.parseInt(parcelIdText);

                String[] statuses = {
                        "Booked",
                        "Picked Up",
                        "In Transit",
                        "Out for Delivery",
                        "Delivered"
                };

                String newStatus =
                        (String) JOptionPane.showInputDialog(
                                frame,
                                "Select New Status:",
                                "Update Status",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                statuses,
                                statuses[0]
                        );

                if (newStatus == null) {
                    return;
                }

                String location =
                        JOptionPane.showInputDialog(
                                frame,
                                "Enter Current Location:"
                        );

                if (location == null || location.isEmpty()) {
                    return;
                }

                Connection connection =
                        DatabaseConnection.getConnection();

                // Update current parcel status
                String updateSql =
                        "UPDATE parcels SET status = ? " +
                                "WHERE parcel_id = ?";

                PreparedStatement updateStatement =
                        connection.prepareStatement(updateSql);

                updateStatement.setString(1, newStatus);
                updateStatement.setInt(2, parcelId);

                int rows =
                        updateStatement.executeUpdate();

                if (rows == 0) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Parcel not found!"
                    );

                    updateStatement.close();
                    connection.close();

                    return;
                }

                // Add tracking history
                String trackingSql =
                        "INSERT INTO tracking " +
                                "(parcel_id, status, location) " +
                                "VALUES (?, ?, ?)";

                PreparedStatement trackingStatement =
                        connection.prepareStatement(trackingSql);

                trackingStatement.setInt(1, parcelId);
                trackingStatement.setString(2, newStatus);
                trackingStatement.setString(3, location);

                trackingStatement.executeUpdate();

                trackingStatement.close();
                updateStatement.close();
                connection.close();

                JOptionPane.showMessageDialog(
                        frame,
                        "Parcel status updated successfully!"
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Parcel ID must be a number!"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Error: " + ex.getMessage()
                );
            }
        });

// ================= VIEW PARCELS =================

        viewParcelsButton.addActionListener(e -> {

            try {

                Connection connection =
                        DatabaseConnection.getConnection();

                String sql =
                        "SELECT p.parcel_id, " +
                                "c.name AS sender_name, " +
                                "p.receiver_name, " +
                                "p.source, " +
                                "p.destination, " +
                                "p.parcel_type, " +
                                "p.booking_date, " +
                                "p.status " +
                                "FROM parcels p " +
                                "JOIN customers c ON p.sender_id = c.customer_id " +
                                "ORDER BY p.parcel_id";

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery();

                StringBuilder parcels = new StringBuilder();

                while (resultSet.next()) {

                    parcels.append(
                            "Tracking ID: "
                                    + resultSet.getInt("parcel_id")
                                    + "\n"
                                    + "Sender: "
                                    + resultSet.getString("sender_name")
                                    + "\n"
                                    + "Receiver: "
                                    + resultSet.getString("receiver_name")
                                    + "\n"
                                    + "From: "
                                    + resultSet.getString("source")
                                    + "\n"
                                    + "To: "
                                    + resultSet.getString("destination")
                                    + "\n"
                                    + "Parcel Type: "
                                    + resultSet.getString("parcel_type")
                                    + "\n"
                                    + "Booking Date: "
                                    + resultSet.getDate("booking_date")
                                    + "\n"
                                    + "Status: "
                                    + resultSet.getString("status")
                                    + "\n"
                                    + "--------------------------------\n\n"
                    );
                }

                if (parcels.length() == 0) {

                    parcels.append("No parcels found.");

                }

                JTextArea textArea =
                        new JTextArea(parcels.toString());

                textArea.setEditable(false);
                textArea.setFont(new Font("Arial", Font.PLAIN, 14));

                JScrollPane scrollPane =
                        new JScrollPane(textArea);

                scrollPane.setPreferredSize(
                        new Dimension(450, 400)
                );

                JOptionPane.showMessageDialog(
                        frame,
                        scrollPane,
                        "All Parcels",
                        JOptionPane.INFORMATION_MESSAGE
                );

                resultSet.close();
                statement.close();
                connection.close();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Error: " + ex.getMessage()
                );
            }
        });
        // ================= EXIT =================

        exitButton.addActionListener(e ->
                System.exit(0)
        );


        // ================= MAIN WINDOW =================

        frame.add(title, BorderLayout.NORTH);

        panel.add(addCustomerButton);
        panel.add(bookParcelButton);
        panel.add(trackParcelButton);
        panel.add(updateStatusButton);
        panel.add(viewParcelsButton);
        panel.add(exitButton);

        frame.add(panel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
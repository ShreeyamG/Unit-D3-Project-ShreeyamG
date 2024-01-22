import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Time;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class Main {
    private static ArrayList<Task> tasks = new ArrayList<>();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static JList<String> taskList;

    private static void GUI() {
        JFrame jFrame = new JFrame("Task Manager");
        jFrame.setLayout(new BorderLayout());
        jFrame.setSize(550, 400);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Task Manager");
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        label.setBorder(border);
        label.setPreferredSize(new Dimension(150, 50));
        label.setHorizontalAlignment(JLabel.CENTER);

        taskList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(taskList);
        mainPanel.add(label, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(1000, 100));

        JTextField taskNameField = new JTextField(15);
        JTextField dateField = new JTextField(10);
        JFormattedTextField timeField = new JFormattedTextField(new SimpleDateFormat("hh:mm a"));
        timeField.setColumns(10);
        JButton addButton = new JButton("Add Task");
        JButton deleteButton = new JButton("Delete Task");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTask(taskNameField.getText(), dateField.getText(), timeField.getText());
                updateTaskList();
                taskNameField.setText("");
                dateField.setText("");
                timeField.setText("");
            }
        });
      
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    tasks.remove(selectedIndex);
                    updateTaskList();
                }
            }
        });

      JButton organizeButton = new JButton("Organize by Due Date");
      organizeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              Collections.sort(tasks);
              updateTaskList();
          }
      });
     


        inputPanel.add(new JLabel("Task Name: "));
        inputPanel.add(taskNameField);
        inputPanel.add(new JLabel("Due Date (yyyy-MM-dd): "));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Time Due: "));
        inputPanel.add(timeField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(organizeButton);

        jFrame.add(mainPanel, BorderLayout.CENTER);
        jFrame.add(inputPanel, BorderLayout.SOUTH);

        jFrame.setVisible(true);
    }

    private static void addTask(String taskName, String dateString, String timeString) {
        try {
            Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            Date timeDate = new SimpleDateFormat("hh:mm a").parse(timeString);
            long timeInMilliseconds = timeDate.getTime();
            Time time = new Time(timeInMilliseconds);
            Task task = new Task(taskName, dueDate, time);
            tasks.add(task);
           
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date or time format. Please use yyyy-MM-dd for date and hh:mm a for time.");
        }
    }

    private static void updateTaskList() {
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task.toString());
        }
    }

    public static void main(String[] args) {
        loadTasksFromFile();
      GUI();
    }

    private static class Task implements Comparable<Task> {
        private String name;
        private Date dueDate;
        private Time time;

        public Task(String name, Date dueDate, Time time) {
            this.name = name;
            this.dueDate = dueDate;
            this.time = time;
        }

        public int compareTo(Task otherTask) {
            int dateComparison = this.dueDate.compareTo(otherTask.dueDate);
            if (dateComparison == 0) {
                return this.time.compareTo(otherTask.time);
            }
            return dateComparison;
        }

        public String toString() {
            return name + " - Due: " + new SimpleDateFormat("yyyy-MM-dd").format(dueDate) + " - Time: " + time.toString();
        }
    }

  private static final String TASKS_FILE = "tasks.txt";

 
  private static void saveTasksToFile() {
      try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TASKS_FILE))) {
          outputStream.writeObject(tasks);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @SuppressWarnings("unchecked")
  private static void loadTasksFromFile() {
      File file = new File("tasks.txt");
      if (file.exists()) {
          try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TASKS_FILE))) {
              tasks = (ArrayList<Task>) inputStream.readObject();
              updateTaskList();
          } catch (IOException | ClassNotFoundException e) {
              e.printStackTrace();
          }
      }
  }

}

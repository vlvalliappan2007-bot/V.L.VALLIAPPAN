import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Valliappan extends JFrame {
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<CartItem> cart = new ArrayList<>();
    JTextField usernameField;
    JPasswordField passwordField;
    JPanel mainPanel;

    static class Product {
        String name;
        double price;
        Product(String name,double price) {
            this.name=name;
            this.price=price;
        }
    }

    static class CartItem {
        Product product;
        int quantity;
        CartItem(Product product,int quantity) {
            this.product=product;
            this.quantity=quantity;
        }
    }

    public Valliappan() {
        setTitle("VALLIAPPAN");
        setSize(900,650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadProducts();
        showLogin();
    }

    void loadProducts() {
        products.add(new Product("Smartphone",15000));
        products.add(new Product("Laptop",55000));
        products.add(new Product("Headphones",2000));
        products.add(new Product("Smart Watch",3000));
        products.add(new Product("Bluetooth Speaker",2500));
        products.add(new Product("Gaming Mouse",1200));
        products.add(new Product("Keyboard",1500));
        products.add(new Product("Backpack",1800));
        products.add(new Product("Running Shoes",3500));
        products.add(new Product("T-Shirt",800));
        products.add(new Product("Power Bank",1200));
        products.add(new Product("USB Cable",300));
    }

    void showLogin() {
        JPanel panel=new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(10,10,10,10);

        JLabel title=new JLabel("VALLIAPPAN");
        title.setFont(new Font("Arial",Font.BOLD,32));
        title.setForeground(new Color(30,80,180));

        usernameField=new JTextField(20);
        passwordField=new JPasswordField(20);

        JButton loginButton=new JButton("LOGIN");
        JButton registerButton=new JButton("CREATE NEW ACCOUNT");

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        panel.add(title,gbc);

        gbc.gridwidth=1;
        gbc.gridx=0;
        gbc.gridy=1;
        panel.add(new JLabel("Username:"),gbc);

        gbc.gridx=1;
        panel.add(usernameField,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        panel.add(new JLabel("Password:"),gbc);

        gbc.gridx=1;
        panel.add(passwordField,gbc);

        gbc.gridx=0;
        gbc.gridy=3;
        panel.add(loginButton,gbc);

        gbc.gridx=1;
        panel.add(registerButton,gbc);

        loginButton.addActionListener(e->login());
        registerButton.addActionListener(e->showRegister());

        setContentPane(panel);
        revalidate();
        repaint();
    }

    void login() {
        String username=usernameField.getText().trim();
        String password=new String(passwordField.getPassword());

        if(username.isEmpty()||password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Enter username and password");
            return;
        }

        if(checkLogin(username,password)) {
            showHome(username);
        } else {
            JOptionPane.showMessageDialog(this,"Invalid username or password");
        }
    }

    void showRegister() {
        JPanel panel=new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(10,10,10,10);

        JLabel title=new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Arial",Font.BOLD,28));

        JTextField user=new JTextField(20);
        JPasswordField pass=new JPasswordField(20);
        JPasswordField confirm=new JPasswordField(20);

        JButton create=new JButton("CREATE ACCOUNT");
        JButton back=new JButton("BACK TO LOGIN");

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        panel.add(title,gbc);

        gbc.gridwidth=1;
        gbc.gridx=0;
        gbc.gridy=1;
        panel.add(new JLabel("Username:"),gbc);

        gbc.gridx=1;
        panel.add(user,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        panel.add(new JLabel("Password:"),gbc);

        gbc.gridx=1;
        panel.add(pass,gbc);

        gbc.gridx=0;
        gbc.gridy=3;
        panel.add(new JLabel("Confirm Password:"),gbc);

        gbc.gridx=1;
        panel.add(confirm,gbc);

        gbc.gridx=0;
        gbc.gridy=4;
        panel.add(create,gbc);

        gbc.gridx=1;
        panel.add(back,gbc);

        create.addActionListener(e->{
            String username=user.getText().trim();
            String password=new String(pass.getPassword());
            String confirmPassword=new String(confirm.getPassword());

            if(username.isEmpty()||password.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Please fill all fields");
                return;
            }

            if(username.contains(",")||username.contains(" ")) {
                JOptionPane.showMessageDialog(this,"Username cannot contain spaces or commas");
                return;
            }

            if(!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,"Passwords do not match");
                return;
            }

            if(userExists(username)) {
                JOptionPane.showMessageDialog(this,"Username already exists");
                return;
            }

            registerUser(username,password);
            JOptionPane.showMessageDialog(this,"Account created successfully");
            showLogin();
        });

        back.addActionListener(e->showLogin());

        setContentPane(panel);
        revalidate();
        repaint();
    }

    boolean userExists(String username) {
        File file=new File("users.txt");

        if(!file.exists()) {
            return false;
        }

        try(BufferedReader br=new BufferedReader(new FileReader(file))) {
            String line;

            while((line=br.readLine())!=null) {
                String[] data=line.split(",",2);

                if(data.length==2&&data[0].equals(username)) {
                    return true;
                }
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(this,"Error reading users file");
        }

        return false;
    }

    void registerUser(String username,String password) {
        try(PrintWriter out=new PrintWriter(new FileWriter("users.txt",true))) {
            out.println(username+","+password);
        } catch(IOException e) {
            JOptionPane.showMessageDialog(this,"Error creating account");
        }
    }

    boolean checkLogin(String username,String password) {
        File file=new File("users.txt");

        if(!file.exists()) {
            return false;
        }

        try(BufferedReader br=new BufferedReader(new FileReader(file))) {
            String line;

            while((line=br.readLine())!=null) {
                String[] data=line.split(",",2);

                if(data.length==2&&data[0].equals(username)&&data[1].equals(password)) {
                    return true;
                }
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(this,"Error reading users file");
        }

        return false;
    }

    void showHome(String username) {
        mainPanel=new JPanel(new BorderLayout());

        JPanel top=new JPanel(new BorderLayout());
        JLabel title=new JLabel("VALLIAPPAN");
        title.setFont(new Font("Arial",Font.BOLD,26));

        JLabel welcome=new JLabel("Welcome, "+username);

        JButton cartButton=new JButton("CART");
        JButton logoutButton=new JButton("LOGOUT");

        JPanel right=new JPanel();
        right.add(welcome);
        right.add(cartButton);
        right.add(logoutButton);

        top.add(title,BorderLayout.WEST);
        top.add(right,BorderLayout.EAST);

        JPanel productPanel=new JPanel(new GridLayout(0,3,15,15));

        for(Product p:products) {
            JPanel card=new JPanel();
            card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel name=new JLabel(p.name);
            name.setFont(new Font("Arial",Font.BOLD,18));
            name.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel price=new JLabel("₹"+p.price);
            price.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton add=new JButton("ADD TO CART");
            add.setAlignmentX(Component.CENTER_ALIGNMENT);

            add.addActionListener(e->addToCart(p));

            card.add(Box.createVerticalStrut(15));
            card.add(name);
            card.add(Box.createVerticalStrut(10));
            card.add(price);
            card.add(Box.createVerticalStrut(10));
            card.add(add);
            card.add(Box.createVerticalStrut(15));

            productPanel.add(card);
        }

        mainPanel.add(top,BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(productPanel),BorderLayout.CENTER);

        cartButton.addActionListener(e->showCart());
        logoutButton.addActionListener(e->{
            cart.clear();
            showLogin();
        });

        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    void addToCart(Product product) {
        for(CartItem item:cart) {
            if(item.product==product) {
                item.quantity++;
                JOptionPane.showMessageDialog(this,"Added to cart");
                return;
            }
        }

        cart.add(new CartItem(product,1));
        JOptionPane.showMessageDialog(this,"Added to cart");
    }

    void showCart() {
        JPanel panel=new JPanel(new BorderLayout());

        JPanel itemsPanel=new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel,BoxLayout.Y_AXIS));

        double total=0;

        for(CartItem item:cart) {
            JPanel row=new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel label=new JLabel(
                item.product.name+"  ₹"+item.product.price+
                " x "+item.quantity
            );

            JButton plus=new JButton("+");
            JButton minus=new JButton("-");
            JButton remove=new JButton("REMOVE");

            plus.addActionListener(e->{
                item.quantity++;
                showCart();
            });

            minus.addActionListener(e->{
                item.quantity--;

                if(item.quantity<=0) {
                    cart.remove(item);
                }

                showCart();
            });

            remove.addActionListener(e->{
                cart.remove(item);
                showCart();
            });

            row.add(label);
            row.add(plus);
            row.add(minus);
            row.add(remove);

            itemsPanel.add(row);

            total+=item.product.price*item.quantity;
        }

        JLabel totalLabel=new JLabel("Total: ₹"+total);
        totalLabel.setFont(new Font("Arial",Font.BOLD,20));

        JButton checkout=new JButton("CHECKOUT");
        JButton clear=new JButton("CLEAR CART");
        JButton back=new JButton("BACK");

        JPanel bottom=new JPanel();
        bottom.add(totalLabel);
        bottom.add(checkout);
        bottom.add(clear);
        bottom.add(back);

        checkout.addActionListener(e->checkout());
        clear.addActionListener(e->{
            cart.clear();
            showCart();
        });
        back.addActionListener(e->showHome("User"));

        panel.add(new JScrollPane(itemsPanel),BorderLayout.CENTER);
        panel.add(bottom,BorderLayout.SOUTH);

        setContentPane(panel);
        revalidate();
        repaint();
    }

    void checkout() {
        if(cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Cart is empty");
            return;
        }

        double total=0;

        for(CartItem item:cart) {
            total+=item.product.price*item.quantity;
        }

        JOptionPane.showMessageDialog(
            this,
            "Order placed successfully!\nTotal Amount: ₹"+total
        );

        cart.clear();
        showLogin();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            Valliappan app=new Valliappan();
            app.setVisible(true);
        });
    }
}
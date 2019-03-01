
package ptt;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import java.sql.*;

public class PttHandler extends Thread {
  static Vector handlers = new Vector(10);
  static Vector username = new Vector(10);
  static Vector chat_handlers[] = new Vector [100];
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  String classname="com.mysql.jdbc.Driver";
  String jdbcURL="jdbc:mysql://localhost/[your dbname]?useSSL=false";
  String u="[your username]";
  String p="[your password]";
  Connection conn=null;
  static String line;
  String write_title="";
  String write_content="";
  String write_content_next="";
  static int chat_count = 0;
  int chat_count_next;
  int chat_id;
  int article_id;
  static String chat_name[] = new String [100];
  String username_setup = "";
  String password_setup = "";
  String username_login = "";
  String password_login = "";
   
  public PttHandler(Socket socket) throws IOException {
      this.socket = socket;
      in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"big5"));
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"big5")); 
  }

public void run() {
        synchronized(handlers) {
             handlers.addElement(this);
            // add() not found in Vector class
        }
try{
    Class.forName(classname).newInstance();
    this.out.println("��J /login �i��n�J");
    this.out.flush();
    this.out.println("��J /setup �إ߷|��");
    this.out.flush();
    while(!(line = in.readLine()).equalsIgnoreCase("/quit")) {
        if(line.equals("/setup")){
            while(true){
             this.out.print("�إ߼ʺ١G");
             this.out.flush();
             if(!(line=in.readLine()).equalsIgnoreCase("")){
                username_setup = line;
                conn=DriverManager.getConnection(jdbcURL,u,p);
                Statement setup_Statement=conn.createStatement();
                String username_setup_Query="SELECT username FROM ptt_member WHERE username =" + "'" + username_setup + "'";
                ResultSet rs_username_setup=setup_Statement.executeQuery(username_setup_Query);
                   if(rs_username_setup.next()){
                     this.out.println("�ʺ٤w�s�b");
                     this.out.flush();
                     username_setup = null;
                     setup_Statement.close();
                     conn.close();
                    }

                 if(username_setup != null){
                  this.out.print("�إ߱K�X�G");
                  this.out.flush();
                  password_setup = in.readLine();
                  String member_insert_Query="INSERT INTO ptt_member (username,password) VALUES ('"+username_setup+"','"+password_setup+"')";
                  setup_Statement.executeUpdate(member_insert_Query);
                  this.out.println("�إߦ��\");
                  this.out.flush();
                  setup_Statement.close();
                  conn.close();
                  break;
                 }
            }
             else{
               this.out.println("�Э��s�إ߼ʺ�");
               this.out.flush();
             }
          }
        }

         if(line.equals("/login")){
             while(true){
                 int m = 0;
                this.out.print("��J�ʺ١G");
                this.out.flush();
                username_login=in.readLine();
                if(username.size() > 0){
                    for(int o=0 ; o<username.size() ; o++){
                        if(username_login.equals(username.get(o))){
                           username_login = null;
                           m = 1;
                           this.out.println("���ʺ٤w�n�J");
                           this.out.flush();
                           this.out.println("�Э��s��J�ʺ�");
                           this.out.flush();
                           break;
                        }
                    }
                }
                if(m == 0){
                    conn=DriverManager.getConnection(jdbcURL,u,p);
                    Statement login_Statement=conn.createStatement();
                    String username_login_Query="SELECT username FROM ptt_member WHERE username =" + "'" + username_login + "'";
                    ResultSet rs_username_login=login_Statement.executeQuery(username_login_Query);
                    if(!(rs_username_login.next())){
                       this.out.println("�d�L���ʺ�");
                       this.out.flush();
                       username_login = null;
                       m = 1;
                       login_Statement.close();
                       conn.close();
                       this.out.println("�Э��s��J�ʺ�");
                       this.out.flush();
                    }
                }
                if(m == 0){
                    if(username_login != null){
                      this.out.print("��J�K�X�G");
                      this.out.flush();
                      password_login=in.readLine();
                      conn=DriverManager.getConnection(jdbcURL,u,p);
                      Statement login_Statement=conn.createStatement();
                      String password_login_Query="SELECT password FROM ptt_member WHERE password =" + "'" + password_login + "'";
                      ResultSet rs_password_login=login_Statement.executeQuery(password_login_Query);   
                      if(rs_password_login.next()){
                          this.out.println("�n�J���\");
                          this.out.flush();
                          username.addElement(username_login);
                          login_Statement.close();
                          conn.close();
                          while(true){
                              this.out.println("��J /write �i�s�ؤ峹");
                              this.out.flush();
                              this.out.println("��J /article �i�d�ݤ峹");
                              this.out.flush();
                              this.out.println("��J /newchat �i�s�ز�ѫ�");
                              this.out.flush();
                              this.out.println("��J /chat �i�d�ݲ�ѫ�");
                              this.out.flush();
                              this.out.println("��J /logout �i�n�X");
                              this.out.flush();
                              
                            if((line = in.readLine()).equals("/write")) {
                                this.out.println("�峹���D�G");        
                                this.out.flush();
                                    write_title = in.readLine();
                                    this.out.println("�Y�n�����峹�Цb���UEnter����J /end �G");        
                                    this.out.flush();
                                    this.out.println("�峹���e�G");
                                    this.out.flush();
                                    while(!(line = in.readLine()).equals("/end")){
                                        write_content = line;
                                        write_content_next += write_content + "\r\n";
                                    }
                                    if(write_content_next != ""){
                                         conn=DriverManager.getConnection(jdbcURL,u,p);
                                         Statement write_Statement=conn.createStatement();
                                         String write_insert_Query="INSERT INTO ptt_article (id,title,content,username) VALUES (null,'"+write_title+"','"+write_content_next+"','"+username_login+"')";
                                         write_Statement.executeUpdate(write_insert_Query);
                                         write_Statement.close();
                                         conn.close();
                                         this.out.println("�峹�s�ا���");
                                         this.out.flush();
                                    }
                            }
                            
                            if(line.equals("/article")) {
                                int h = 0;
                                int write_count = 0;
                                conn=DriverManager.getConnection(jdbcURL,u,p);
                                Statement article_Statement=conn.createStatement();
                                String article_Query="SELECT id,title,username FROM ptt_article";
                                ResultSet rs_article=article_Statement.executeQuery(article_Query); 
                                while(h == 0){
                                    while(rs_article.next()){
                                        for(int r = 1 ; r<=3 ; r++){
                                            if(r > 1){
                                                this.out.print("\t");
                                                this.out.flush();
                                            }
                                            this.out.print(rs_article.getString(r));
                                            this.out.flush();
                                        }
                                       this.out.print("\r\n");
                                       this.out.flush();
                                       write_count = 1;
                                       h = 1;
                                    }
                                    if(write_count == 0){
                                       this.out.println("�ثe�L�峹");
                                       this.out.flush();
                                       article_Statement.close();
                                       conn.close();
                                    }
                                    break;
                            }
                                if(h == 1){
                                    this.out.println("��J /look �i�[�ݤ峹");
                                    this.out.flush();
                                    if(in.readLine().equals("/look")){
                                        this.out.println("��J�n�[�ݪ��峹�s���G");
                                        this.out.flush();
                                        article_id = Integer.parseInt(in.readLine());
                                        String article_look_Query="SELECT content FROM ptt_article WHERE id=" + "'" + article_id + "'";
                                        ResultSet rs_article_look=article_Statement.executeQuery(article_look_Query);
                                        if(rs_article_look.next()){
                                            this.out.println(rs_article_look.getString(1));
                                            this.out.flush();
                                            this.out.print("\r\n");
                                            this.out.flush();
                                        }
                                        else{
                                            this.out.println("���s�����s�b");
                                            this.out.flush();
                                        }
                                    }
                                 }
                            }
                            
                            if(line.equals("/newchat")) {
                                while(!(line.equals("/exit"))){
                                  this.out.print("��J��ѫǦW�١G");
                                  this.out.flush();
                                  this.out.print("��J /exit �i�����إߡG");
                                  this.out.flush();
                                  if(in.readLine().equals("/exit")){
                                      break;
                                  }
                                  chat_count++;
                                  chat_count_next=chat_count;
                                  chat_name[chat_count_next]=in.readLine();
                                  this.out.println("�s�ئ��\");
                                  this.out.flush();
                                  this.out.println("��J /exit �i���}��ѫ�");
                                  this.out.flush();
                                  chat_handlers[chat_count_next] = new Vector(10);
                                  chat_handlers[chat_count_next].addElement(this);
                                  while(!(line=in.readLine()).equals("/exit")) {
                                     for(int i = 0; i < chat_handlers[chat_count_next].size(); i++) {
                                          synchronized(chat_handlers[chat_count_next]) {
                                             PttHandler handler = (PttHandler)chat_handlers[chat_count_next].elementAt(i);
                                             if(handler!=this){
                                                 handler.out.println(username_login + "�G" + line);
                                                 handler.out.flush();
                                              }
                                          }
                                      }
                                  }
                                  synchronized(chat_handlers[chat_count_next]) {
                                    chat_handlers[chat_count_next].removeElement(this);
                                  }
                                }
                              }
                            
                            if(line.equals("/chat")) {
                                int u=0;
                                while(u == 0){
                                    if(chat_count != 0){
                                       for(int z=1 ; z < chat_count + 1 ; z++){
                                          if(chat_handlers[z].size() > 0){
                                            this.out.println(z + "\t" + chat_name[z]);
                                            this.out.flush();
                                          }
                                       }
                                       break;
                                    }
                                    if(chat_count == 0){
                                       this.out.println("�ثe�L��ѫ�");
                                       this.out.flush();
                                       u = 1;
                                       break;
                                    }
                                }
                                if(u == 0){
                                    this.out.println("��J /join �i�i�J��ѫ�");
                                    this.out.flush();                        
                                    this.out.println("��J /exit �i��^");
                                    this.out.flush();
                                    while(!(line = in.readLine()).equals("/exit")){
                                       if(line.equals("/join")){
                                           this.out.println("��J�n�i�J����ѫǽs���A�Y��J /exit �i�����i�J�G");
                                           this.out.flush();
                                           if(!(in.readLine().equals("/exit"))){
                                            chat_id = Integer.parseInt(in.readLine());
                                            chat_handlers[chat_id].addElement(this);
                                            this.out.println("�i�J���\�G");
                                            this.out.flush();
                                            this.out.println("��J�@/exit�@�i���}��ѫ�");
                                            this.out.flush();
                                            while(!(line=in.readLine()).equals("/exit")) {
                                               for(int i = 0; i < chat_handlers[chat_id].size(); i++) {
                                                    synchronized(chat_handlers[chat_id]) {
                                                       PttHandler handler = (PttHandler)chat_handlers[chat_id].elementAt(i);
                                                       if(handler!=this){
                                                           handler.out.println(username_login + "�G" + line);
                                                           handler.out.flush();
                                                       }
                                                    }
                                                }
                                            }
                                            synchronized(chat_handlers[chat_id]) {
                                              chat_handlers[chat_id].removeElement(this);
                                            }
                                            break;
                                       }
                                           this.out.println("��J /join �i�i�J��ѫ�");
                                           this.out.flush();                        
                                           this.out.println("��J /exit �Y�i��^");
                                           this.out.flush();
                                       }
                                    }
                            }
                          }

                            if(line.equals("/logout")){
                               username.removeElement(username_login);
                               break;
                            }                
                            }
                             break;
                        } 
                      else {
                            this.out.println("�K�X���~�A�n�J����");
                            this.out.flush(); 
                            this.out.println("�Э��s�n�J");
                            this.out.flush(); 
                            login_Statement.close();
                            conn.close();
                            }
                   } 
             }
          } 
        }
         this.out.println("��J /login �i��n�J");
         this.out.flush();
         this.out.println("��J /setup �إ߷|��");
         this.out.flush();
 }
}
       catch (Exception e) {
         System.out.println(e);
       }
finally{
     try{
        in.close();
        out.close();
        socket.close();
       }
    catch(IOException ioe) {
        
    }      
    finally {
        synchronized(handlers) {
              username.removeElement(username_login);
            }
        synchronized(handlers) {
              handlers.removeElement(this);
            }
            }
        }
    }
}

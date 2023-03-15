package clj_owasp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class top10_insufficient_logging_and_monitoring {
  static Map<String, String> db = new HashMap<String, String>(){
    {put("arthur.almeida", "senha123");}
  };

  public static void main(String[] args) {
    System.out.println(login_old("arthur.almeida",
        "senha321", "187.56.25.04"));
    System.out.println(login("arthur.almeida",
        "senha321", "187.56.25.04"));
  }

  //O problema aqui é que não estamos logando falhas de login
  public static boolean login_old(String username, String password,
      String ipaddress){
    if (password.equals(db.get(username))){
      return true;
    } else return false;
  }

  //Solução: sempre registrar falhas de login, e não apenas no
  //Console do servidor, e sim em um serviço de logging apropriado!
  public static boolean login(String username, String password,
      String ipaddress){
    if (password.equals(db.get(username))){
      return true;
    } else {
      System.err.println("O usuário " + username + " com ip "+ ipaddress +
          " tentou logar na aplicação as "+ LocalDateTime.now());
      sendToLoggingService(username, password, ipaddress);
      return false;
    }
  }

  public static void sendToLoggingService(String username,
      String password, String ipaddress){
    //envia registro de falha para servico de logging
  }

}

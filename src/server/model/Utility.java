package server.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public Utility(){}

    public void exportData(List<UserModel> users){
        try(
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File("res/data.dat")));
        ) {
            for(UserModel user: users){
                outputStream.writeObject(user);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UserModel> readData(String filename){
        List<UserModel> users = new ArrayList<>();
        try(
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(filename)));
        ) {
            UserModel u;
            while (true){
                try {
                    u = (UserModel)inputStream.readObject();
                    users.add(u);
                } catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }
}

package pa.centric.util.uidutil;
import net.minecraft.client.Minecraft;
import pa.centric.Centric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class UIDUtils {
    public static final File directory = new File(Minecraft.getInstance().gameDir + "\\Centric\\Client_1_16_5\\");
    public static void uidgen() {

        File file = new File(UIDUtils.directory + "\\uid.cc" );
        int random_number;
        try {
            // �������� ��������� ����� �� �����
            Scanner scanner = new Scanner(file);
            random_number = scanner.nextInt();
            System.out.println(Centric.prefix + "������� ����� ��������������� �����: " + random_number);
        } catch (FileNotFoundException e) {
            // ���� ���� �� ������, ���������� ����� �����
            Random rand = new Random();
            random_number = rand.nextInt(2000);
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(Integer.toString(random_number));
                writer.close();
                System.out.println(Centric.prefix + "������������� ����� ��������� �����: " + random_number);
            } catch (IOException ex) {
                System.out.println(Centric.prefix + "������ ��� ������ � ����: " + ex.getMessage());
            }
        }
    }
}

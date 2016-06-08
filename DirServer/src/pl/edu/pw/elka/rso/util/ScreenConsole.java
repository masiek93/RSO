package pl.edu.pw.elka.rso.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScreenConsole {

        private final BufferedReader reader;
        private static ScreenConsole instance;
        
        public static ScreenConsole getInstance() {
            if(instance == null) {
                instance = new ScreenConsole();
            }
            return instance;
        }
        

        private ScreenConsole() {
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
        }


        public String readLine() {
            if (System.console() != null) {
                return System.console().readLine();
            }
            try {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void printf(String format, Object ... args) {
            System.out.printf(format, args);
        }

        public void die(String format, Object ... args) {
            printf(format, args);
            printf("\nexisting.");
            System.exit(1);
        }

    }
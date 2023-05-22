package edu.ufl.team5;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintStream;
import java.util.*;

public class TestClient {
    private static PrintStream out = System.out;
    private static Scanner sc = new Scanner(System.in);

    private static Client client = ClientBuilder.newClient();
    private static String domain = "http://localhost:8080";
    private static String servicePath = "/InventoryService-1.0-SNAPSHOT/";

    public static void main(String[] args) {

        out.println("Enter C to create a purchase order, R to receive a shipment, L to list open orders, Q to Quit");
        String nextLine;
        while (!(nextLine = sc.next()).equals("Q")) {
            switch (nextLine) {
                case "C":
                    stepC();
                    break;
                case "R":
                    stepR();
                    break;
                case "L":
                    stepL();
                    break;
                default:
                    out.println("Enter C to create a purchase order, R to receive a shipment, L to list open orders, Q to Quit");
                    continue;
            }
            out.println("Enter C to create a purchase order, R to receive a shipment, L to list open orders, Q to Quit");
        }
        sc.close();
        out.close();
    }

    private static void stepC() {
        String nextLine;
        int vid;
        List<String> poDetails = new ArrayList<>();
        boolean created = false;
        out.print("Please Enter Vid: ");
        vid = sc.nextInt();
        while (!created) {
            int pid, quantity;
            out.print("Please Enter Pid: ");
            pid = sc.nextInt();
            out.print("Please Enter Quantity: ");
            quantity = sc.nextInt();
            poDetails.add(pid + "," + quantity);

            out.println("Enter N for an additional line item, or C to create the purchase order: ");
            nextLine = sc.next();
            if (nextLine.equalsIgnoreCase("N")) {
                continue;
            } else if (nextLine.equalsIgnoreCase("C")) {
                createPo(vid, poDetails);
                created = true;
            } else {  //// check user's input, if user enters a string other than "N" or "C" or "n" or "c", tell the user to re-enter correct command;
                out.println("Enter N for an additional line item, or C to create the purchase order: ");
                while (true) {
                    String tmp = sc.next();
                    if (tmp.equalsIgnoreCase("C")) {
                        createPo(vid, poDetails);
                        created = true;
                        break;
                    } else if (tmp.equalsIgnoreCase("N")) {
                        break;
                    } else {
                        out.println("Enter N for an additional line item, or C to create the purchase order: ");
                    }
                }
            }
        }
    }

    private static void stepR() {
        out.print("Please Enter Poid: ");
        String poid = sc.next();
        WebTarget target = client.target(domain).path(servicePath + "receive/" + poid);
        Builder rq = target.request();
        out.println(rq.get(String.class));
//        out.println(poClient.receive(sc.nextInt()));
    }

    private static void stepL() {
        out.print("Please Enter Vid: ");
        String vid = sc.next();
        WebTarget target = client.target(domain).path(servicePath + "inventory-list/" + vid);
        Builder rq = target.request();
        String response = rq.get(String.class);
        JSONArray jsa = new JSONArray(response);
        if (jsa.length() == 0) {
            out.println("no open orders!");
            return;
        }
        out.printf("%-10s %-10s %-10s\n", "poid", "vid", "pdate");
        for (Object o : jsa) {
            JSONObject jo = (JSONObject) o;
            String po_id = (String) jo.get("poid");
            String v_id = (String) jo.get("vid");
            String p_date = (String) jo.get("pdate");
            out.printf("%-10s %-10s %-10s\n", po_id, v_id, p_date);
        }
    }


    private static void createPo(int vid, List<String> poDetails) {
        JSONObject jso = new JSONObject();
        jso.put("vid", vid);
        String details = "";
        for (String d : poDetails) {
            details += (" " + d);
        }
        jso.put("po-detail", details);
        String requestStr = jso.toString();
        WebTarget target = client.target(domain).path(servicePath + "create-po");
        String response = target.request().post(Entity.entity(requestStr, MediaType.APPLICATION_JSON), String.class);
//                out.println(poClient.createPO(vid, poDetails));
        out.println(response);
    }


}

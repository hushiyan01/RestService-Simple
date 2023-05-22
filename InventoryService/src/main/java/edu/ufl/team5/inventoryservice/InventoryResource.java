package edu.ufl.team5.inventoryservice;

import edu.ufl.hu.POLib;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import org.json.*;


@Path("/")
public class InventoryResource {

    public static POLib poLib = new POLib("ism6236","ism6236bo");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/inventory-list/{vid}")
    public String inventoryList(@PathParam("vid") String vid) {
        poLib = new POLib("ism6236","ism6236bo");

        List<String> openOrders = poLib.list(Integer.parseInt(vid));
//        List<String> openOrders = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for(String str: openOrders){
            JSONObject jsonObject = new JSONObject();
            String[] attr = str.split(",");
            jsonObject.put("poid",attr[0]);
            jsonObject.put("vid",attr[1]);
            jsonObject.put("pdate",attr[2]);
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    @GET
    @Produces("text/plain")
    @Path("/receive/{poid}")
    public String receive(@PathParam("poid") String poid){
        return poLib.receive(Integer.parseInt(poid));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/create-po")
    public String createOrder(String jsonStr){
        jsonStr = jsonStr.trim();
        JSONObject jso = new JSONObject(jsonStr);
        String vid = jso.get("vid").toString();
        String detailStr =  jso.get("po-detail").toString();
        String trimmed = detailStr.trim();
        String[] details = trimmed.split(" ");
        return poLib.createPO(Integer.parseInt(vid), List.of(details));
    }


}
/**
* The Dispatcher implements DispatcherInterface. 
*
* @author  Oscar Morales-Ponce
* @version 0.15
* @since   02-11-2019 
*/

import java.util.HashMap;
import java.util.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;





public class Dispatcher implements DispatcherInterface {
    HashMap<String, Object> ListOfObjects;
    String TAG = "Dispatcher: ";

    public Dispatcher()
    {
        System.out.println(TAG + "Constructor");
        ListOfObjects = new HashMap<String, Object>();
        System.out.println(TAG + "Constructor return \n");

    }
    
    /* 
    * dispatch: Executes the remote method in the corresponding Object
    * @param request: Request: it is a Json file
    {
        "remoteMethod":"getSongChunk",
        "objectName":"SongServices",
        "param":
          {
              "song":490183,
              "fragment":2
          }
    }
    */
    public String dispatch(String request)
    {
        System.out.println(TAG + ".dispatch");

        JsonObject jsonReturn = new JsonObject();
        JsonParser parser = new JsonParser();
        JsonObject jsonRequest = parser.parse(request).getAsJsonObject();
        
        System.out.println(TAG + ".dispatch try ");

        try {
            // Obtains the object pointing to SongServices
            Object object = ListOfObjects.get(jsonRequest.get("objectName").getAsString());
            Method[] methods = object.getClass().getMethods();
            Method method = null;
            
            // Obtains the method
            for (int i=0; i<methods.length; i++)
            {   
                if (methods[i].getName().equals(jsonRequest.get("remoteMethod").getAsString()))
                    method = methods[i];
            }
            if (method == null)
            {
                jsonReturn.addProperty("error", "Method does not exist");
                return jsonReturn.toString();
            }
            // Prepare the  parameters 
            Class[] types =  method.getParameterTypes();
            Object[] parameter = new Object[types.length];
            String[] strParam = new String[types.length];
            JsonObject jsonParam = jsonRequest.get("param").getAsJsonObject();
            
            
            int j = 0;  
            for (Map.Entry<String, JsonElement>  entry  :  jsonParam.entrySet())
            {
                strParam[j++] = entry.getValue().getAsString();
            }
            // Prepare parameters
            for (int i=0; i<types.length; i++)
            {
                switch (types[i].getCanonicalName())
                {
                    case "java.lang.Long":
                        parameter[i] =  Long.parseLong(strParam[i]);
                        break;
                    case "java.lang.Integer":
                        parameter[i] =  Integer.parseInt(strParam[i]);
                        break;
                    case "String":
                        parameter[i] = new String(strParam[i]);
                        break;
                }
            }
            // Prepare the return
            Class returnType = method.getReturnType();
            String ret = "";
            switch (returnType.getCanonicalName())
                {
                    case "java.lang.Long":
                        ret = method.invoke(object, parameter).toString();
                        break;
                    case "java.lang.Integer":
                        ret = method.invoke(object, parameter).toString();
                        break;
                    case "java.lang.String":
                        ret = (String)method.invoke(object, parameter);
                        break;
                }
                jsonReturn.addProperty("ret", ret);
   
        } catch (InvocationTargetException | IllegalAccessException e)
        {
        //    System.out.println(e);
            jsonReturn.addProperty("error", "Error on " + jsonRequest.get("objectName").getAsString() + "." + jsonRequest.get("remoteMethod").getAsString());
        }
        System.out.println(TAG + ".dispatch: return: json" + "\n");
        return jsonReturn.toString();
    }

    /* 
    * registerObject: It register the objects that handle the request
    * @param remoteMethod: It is the name of the method that 
    *  objectName implements. 
    * @objectName: It is the main class that contaions the remote methods
    * each object can contain several remote methods
    */
    public void registerObject(Object remoteMethod, String objectName)
    {
        System.out.println(TAG + ".registerObject");
        System.out.println("\t args(remoteMethod, " + objectName + ")");

        ListOfObjects.put(objectName, remoteMethod);
        System.out.println(TAG + ".registerObject:" + "return void" + "\n");

    }
    
    /*  Testing
    public static void main(String[] args) {
        // Instance of the Dispatcher
        Dispatcher dispatcher = new Dispatcher();
        // Instance of the services that te dispatcher can handle
        SongDispatcher songDispatcher = new SongDispatcher();
        
        dispatcher.registerObject(songDispatcher, "SongServices");  
    
        // Testing  the dispatcher function
        // First we read the request. In the final implementation the jsonRequest
        // is obtained from the communication module
        try {
            String jsonRequest = new String(Files.readAllBytes(Paths.get("./getSongChunk.json")));
            String ret = dispatcher.dispatch(jsonRequest);
            System.out.println(ret);

            //System.out.println(jsonRequest);
        } catch (Exception e)
        {
            System.out.println(e);
        }
        
    }*/
}

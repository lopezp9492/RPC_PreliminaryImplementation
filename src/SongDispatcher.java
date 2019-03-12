/**
* SongDispatcher is the main responsable for obtaining the songs 
*
* @author  Oscar Morales-Ponce
* @version 0.15
* @since   02-11-2019 
*/

import java.io.IOException;
import java.io.InputStream;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Base64;
import java.io.FileNotFoundException;


public class SongDispatcher
{
	
    String TAG = "SongDispatcher: ";

    static final int FRAGMENT_SIZE = 8192; 
    public SongDispatcher()
    {
        System.out.println(TAG + "Constructor");

    }
    
    /* 
    * getSongChunk: Gets a chunk of a given song
    * @param key: Song ID. Each song has a unique ID 
    * @param fragment: The chunk corresponds to 
    * [fragment * FRAGMENT_SIZE, FRAGMENT_SIZE]
    */
    public String getSongChunk(Long key, Long fragment) throws FileNotFoundException, IOException
    {
        System.out.println(TAG + "getSongChunk");

        byte buf[] = new byte[FRAGMENT_SIZE];

        File file = new File("./" + key);
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.skip(fragment * FRAGMENT_SIZE);
        inputStream.read(buf);
        inputStream.close(); 
        
        
        // Encode in base64 so it can be transmitted 
        System.out.println(TAG + "return buffer: ... ");
        return Base64.getEncoder().encodeToString(buf);
    }
    
    /* 
    * getFileSize: Gets a size of the file
    * @param key: Song ID. Each song has a unique ID 
     */
    public Integer getFileSize(Long key) throws FileNotFoundException, IOException
    {
        System.out.println(TAG + ".getFileSize");
        System.out.println("\t args(" + "key=" + key +")" );


        String path = "./raw/";
        String full_path = path + key;;
        System.out.println(TAG + "getFileSize: path: " + full_path);

        
        File file = new File(full_path);        
        Integer total =  (int)file.length();
        
        System.out.println("\t return total: " + total);

        return total;
    }
    
}

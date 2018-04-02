package songInfoTest;

import com.andriod.deja_vu.MockTime;
import com.andriod.deja_vu.SongInfo;

import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Lenovo on 2018/3/13.
 */

public class updateListener {
    @Test
    public void updateListenerTest(){
        LocalDateTime myTime = LocalDateTime.of(2018,3,13,23,1,1);
        MockTime myMockTime = new MockTime(myTime);
        SongInfo mySongInfo= new SongInfo("whatever", "https://www.dropbox.com/s/wyr2f1b08rfmutg/Amarasiri_Peiris_-_03_-_Kawruwath_Na.mp3?dl=1",myMockTime,"whatever","A");
        mySongInfo.setLastListener("A");
        mySongInfo.setOtherListener("B");
        mySongInfo.updateListeners("C");
        assertEquals("C",mySongInfo.getLastListener());

        mySongInfo.setLastListener("A");
        mySongInfo.setOtherListener("B");
        mySongInfo.updateListeners("A");
        assertEquals("A",mySongInfo.getLastListener());
        assertEquals("B",mySongInfo.getOtherListener());

        mySongInfo.setLastListener("A");
        mySongInfo.setOtherListener("B");
        mySongInfo.updateListeners("B");
        assertEquals("B",mySongInfo.getLastListener());
        assertEquals("A",mySongInfo.getOtherListener());
    }
}

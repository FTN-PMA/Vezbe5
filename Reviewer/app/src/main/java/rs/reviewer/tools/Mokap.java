package rs.reviewer.tools;

import java.util.ArrayList;
import java.util.List;

import model.Cinema;
import rs.reviewer.R;

/**
 * Created by milossimic on 3/21/16.
 */
public class Mokap {

    public static List<Cinema> getCinemas(){
        ArrayList<Cinema> cinemas = new ArrayList<Cinema>();
        Cinema u1 = new Cinema("Arena", "Cineplexx 3D", -1);
        Cinema u2 = new Cinema("Cinestar", "Najnoviji 5D", R.drawable.about);
        Cinema u3 = new Cinema("Jadran", "Tradicionalni u mirnom ambijentu", -1);

        cinemas.add(u1);
        cinemas.add(u2);
        cinemas.add(u3);

        return cinemas;

    }

}

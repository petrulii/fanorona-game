package Vue;

import Modele.Position;

public class PionDeplacable {

    private int position_abs_x = 0, position_abs_y = 0;
    private Position position_reelle = null;
    private Boolean est_relache = true;
    private int joueur = 0;

    public Boolean estRelache() {
        return est_relache;
    }

    public void maintenir(Position p, int x, int y, int j) {
        position_reelle = p;
        est_relache = false;
        joueur = j;
        setPositionAbsolue(x, y);
    }

    public void relacher() {
        est_relache = true;
    }

    public Position getPositionDepart() {
        return position_reelle;
    }

    public void setPositionAbsolue(int x, int y) {
        position_abs_x = x;
        position_abs_y = y;
    }

    public int getPositionXabsolue() {
        return position_abs_x;
    }

    public int getPositionYabsolue() {
        return position_abs_y;
    }

    public int getJoueur() {
        return joueur;
    }

}

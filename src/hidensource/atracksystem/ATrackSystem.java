package hidensource.atracksystem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import processing.core.PApplet;

//--------------------------------------------------------------------------
//--------------------------------------------------------------------------
//TRACK SYSTEM -------------------------------------------------------------
//--------------------------------------------------------------------------
//--------------------------------------------------------------------------
public class ATrackSystem extends PApplet {

  float min_dist = 5;
  float min_mass = 500;
  float max_mass = 1000;
  int siguiente_id = 0;
  ArrayList<ATrackObject> objs;
  ArrayList<ATrackObject> prevobjs;
  ArrayList<ATrackObject> tmp_prevobjs;
  PApplet parent;
  Method onNewTrackObject, onUpdateTrackObject, onDeleteTrackObject;

  public ATrackSystem(PApplet parent) {
    this.parent = parent;

    // Cuando se crea el objeto
    try {
      onNewTrackObject = parent.getClass().getMethod("onNewTrackObject", 
      new Class[] { 
        ATrackObject.class
      }
      );
    } 
    catch (Exception e) {
      // 
    }

    // Cuando se actualiza
    try {
      onUpdateTrackObject = parent.getClass().getMethod(
      "onUpdateTrackObject", new Class[] { 
        ATrackObject.class
      }
      );
    } 
    catch (Exception e) {
      // System.out.println ( "Method not in parent class? " + e ) ;
    }

    // Cuando desaparece
    try {
      onDeleteTrackObject = parent.getClass().getMethod(
      "onDeleteTrackObject", new Class[] { 
        ATrackObject.class
      }
      );
    } 
    catch (Exception e) {
      // System.out.println ( "Method not in parent class? " + e ) ;
    }
  }

  public void minDist(float d) {
    min_dist = d;
  }

  public void compute( ArrayList<ATrackObject> o ) {
    objs = o;
    //int num_objs = objs.size();
    // Si no existen objetos previos se asignan los mismos
    // Solo una vez
    if ( prevobjs == null) {
      for (int i = 0; i < objs.size(); i++) {
        // Nuevos
        ATrackObject ob = ( ATrackObject ) objs.get(i);
        // Asigna ids
        ob.id = siguiente_id;
        ob.px = ob.x;
        ob.py = ob.y;
        ob.time = millis();
        ob.color = color(255);
        objs.set(i, ob);
        onNewTrackObject( ob );
        siguiente_id++;
      }
      prevobjs = objs;
      return;
    }
    // Almacena un temporal de los previos
    tmp_prevobjs = new ArrayList<ATrackObject>( prevobjs );

    int[] remove_id_md = new int[ 0 ];

    // Verifica distancia y otros

    for (int i = 0; i < prevobjs.size(); i++) {

      int id_md = -1;// Almacena el indice de la menor distancia
      float min_min = 9999;// Almacena la distancia mas chica de todas

      // Previos (frame anterior)
      ATrackObject pob = (ATrackObject) prevobjs.get(i);

      for (int j = 0; j < objs.size(); j++) {
        // Nuevos (frame actual)
        ATrackObject ob = (ATrackObject) objs.get(j);

        // Calcula distancia entre blobs
        float dis = dist(pob.x, pob.y, ob.x, ob.y);

        // Calcula diferencia entre masa (cantidad de pixeles)
        // Si la distancia es minima, es el mismo blob
        //float difmass = abs(pob.mass-ob.mass);
        if ( dis <= min_dist ) {
          // Almacena el menor de todos
          if ( dis < min_min ) {
            // actualiza distancia minima
            min_min = dis;
            // Almacena la posicion para recuperarlo luego
            id_md = j;
          }
        }
      }
      if ( id_md != -1 ) {
        // Continuan en escena
        ATrackObject temp_ob = (ATrackObject) objs.get(id_md);

        temp_ob.velx = temp_ob.px - pob.x;
        temp_ob.vely = temp_ob.py - pob.y;
        temp_ob.id = pob.id;
        temp_ob.color = pob.color;
        temp_ob.px = pob.x;
        temp_ob.py = pob.y;
        objs.set(id_md, temp_ob);
        // Almacena los ids que siguen en escena
        remove_id_md = append( remove_id_md, i );
      }
    }
    // Resuelve los objs que ya no estan
    //remove_id_md = sort( remove_id_md );
    for ( int j = remove_id_md.length-1; j >= 0 ; j-- ) {
      tmp_prevobjs.remove( remove_id_md[j] );
    }
    // Asigna a los nuevos o que estan mas lejanos como nuevos
    for (int j = 0; j < objs.size(); j++) {
      // Nuevos (frame actual)
      ATrackObject ob = (ATrackObject) objs.get(j);
      if ( ob.id == -1) {
        ob.id = siguiente_id;
        ob.time = millis();
        ob.color = color(255);
        objs.remove(j);
        objs.add( ob );
        onNewTrackObject( ob );
        siguiente_id++;
      }
      onUpdateTrackObject( ob );
    }
    // Analiza los que ya no estan
    for (int j = 0; j < tmp_prevobjs.size(); j++) {
      ATrackObject del_ob = (ATrackObject) tmp_prevobjs.get(j);
      onDeleteTrackObject(del_ob);
    }
    // Define el previo
    prevobjs = objs;
  }

  public ArrayList<ATrackObject> getTrackedObjects() {
    // Ordenar
    for (int i = 1; i < objs.size(); i++) {
      int j;
      ATrackObject val = (ATrackObject) objs.get(i);
      for (j = i - 1; j > -1; j--) {
        ATrackObject temp = (ATrackObject) objs.get(j);
        if (temp.compareById(val) <= 0) {
          break;
        }
        objs.set(j + 1, temp);
      }
      objs.set(j + 1, val);
    }
    return objs;
  }

  public ATrackObject getByID(int id) {
    for (int i = 0; i < objs.size(); i++) {
      ATrackObject to = (ATrackObject) objs.get(i);
      if (to.id == id) {
        return to;
      }
    }
    return null;
  }

  public boolean contains(int id) {
    for (int i = 0; i < objs.size(); i++) {
      ATrackObject to = (ATrackObject) objs.get(i);
      if (to.id == id) {
        return true;
      }
    }
    return false;
  }

  public int removeByID(int id) {
    int did = 0;
    for (int i = 0; i < objs.size(); i++) {
      ATrackObject to = (ATrackObject) objs.get(i);
      if (to.id == id) {
        did = to.id;
        objs.remove(i);
        return did;
      }
    }
    return did;
  }

  public void onNewTrackObject(ATrackObject to) {
    if (onNewTrackObject != null) {
      try {
        onNewTrackObject.invoke(parent, new Object[] { 
          to
        }
        );
      } 
      catch (Exception e) {
        onNewTrackObject = null;
      }
    }
  }

  public void onUpdateTrackObject(ATrackObject to) {
    if (onUpdateTrackObject != null) {
      try {
        onUpdateTrackObject.invoke(parent, new Object[] { 
          to
        }
        );
      } 
      catch (Exception e) {
        onUpdateTrackObject = null;
      }
    }
  }

  public void onDeleteTrackObject(ATrackObject to) {
    if (onDeleteTrackObject != null) {
      try {
        onDeleteTrackObject.invoke(parent, new Object[] { 
          to
        }
        );
      } 
      catch (Exception e) {
        onDeleteTrackObject = null;
      }
    }
  }
}



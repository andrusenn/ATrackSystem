# Uso
```pde
/*
Ejemplo que utiliza la librería ABlobSystem que depende de OpenCV for processing
y kinect
*/

import gab.opencv.Contour;
import hidensource.ablobsystem.*;
import hidensource.atracksystem.*;
import org.openkinect.freenect.*;
import org.openkinect.processing.*;

ArrayList<ATrackObject> tracked;
ATrackSystem ts;

void setup(){

}
void draw(){
      background(0);
      rawDepth = kinect.getRawDepth();
      for (int i = 0; i < rawDepth.length; i++) {
            float m = rawDepthToCM(rawDepth[i]);
            if (m >= minDepth && m <= maxDepth) {
                  depthImg.pixels[i] = color(255);
            } else {
                  depthImg.pixels[i] = color(0);
            }
      }
      // Draw the thresholded image
      depthImg.updatePixels();
      low_res.copy(depthImg, 0, 0, depthImg.width, depthImg.height, 0, 0, 320*2, 240*2);
      blobs = bs.getBlobs(low_res);
      // Seguimiento de blobs -----------------
      ArrayList o = new ArrayList<ATrackObject>();
      for (int i = 0; i < blobs.size (); i++) {
            ABlob ab = (ABlob) blobs.get(i);
            ATrackObject to = new ATrackObject();
            to.x  = map(ab.cx, 0, low_res.width, 0, width);
            to.y  = map(ab.cy, 0, low_res.height, 0, height);
            to.width = int(ab.width * pw);
            to.height = int(ab.height * ph);
            o.add(to);
      }
      ts.compute(o);
      for (ABlob b : blobs) {
            // Contorno ----------------------------
            beginShape();
            strokeWeight(3);
            stroke(255, 0, 0);
            //noStroke();
            fill(255, 0, 0, 50);
            if (b.containsPoint(100, 100)) {
                  println("si");
            }
            for (PVector point : b.contourPoints) {
                  vertex(point.x * pw, point.y * pw);
            }
            endShape(CLOSE);
            // Box ---------------------------------
            // stroke(255, 255, 0);
            //fill(255, 255, 0, 50);
            //rect(b.x * pw, b.y * pw, b.width * pw, b.height * pw);
      }
}

// Cuando detecta un objeto 
void onNewTrackObject( ATrackObject to ) {
      println("Detectado: " + to.id);
}
// Se actualiza
void onUpdateTrackObject( ATrackObject to ) {
      println(to.x, to.y);
      stroke(255);
      strokeWeight(1);
      fill(255);
      text("id: " + to.id, to.x+20, to.y-20);
      line(to.x-10, to.y, to.x+10, to.y);
      line(to.x, to.y-10, to.x, to.y+10);
}
// Cuando desaparece
void onDeleteTrackObject( ATrackObject to ) {

}
```
# Otro
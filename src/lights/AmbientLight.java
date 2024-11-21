package lights;

import model.Color;
import model.Intersection;
import model.Ray;

public class AmbientLight extends Light{

	public AmbientLight() {
		super();
	}

	public AmbientLight(Color color) {
		super(color);
	}
    
	@Override
    public Color illuminate(Ray ray, Intersection intersection){
		float ka = intersection.material.phong.ka;
		Color iC = intersection.colorAtIntersection; // changed to COLOR FROM INTERSECTION!
		
		// Ambient Coefficient * Intersection Color * Light Color
		Color c = new Color(ka * iC.r * this.color.r, ka * iC.g * this.color.g, ka * iC.b * this.color.b);
		return c;
    }
	
	
}

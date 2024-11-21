package lights;

import java.util.List;

import model.Color;
import model.Intersection;
import model.Ray;
import model.Vector;
import surfaces.Surface;

public class PointLight extends Light{
	public Vector position;

	public PointLight() {
		super();
		position = new Vector();
	}

	public PointLight(Color color, Vector position) {
		super(color);
		this.position = position;
	}
	
	@Override
    public boolean shadowIntersection(Ray ray, List<Surface> surfaceList) {
		for (Surface surface : surfaceList) {
			Intersection intersection = surface.intersect(ray);
			if(intersection != null) {
				if(intersection.distance > 0.0001) { // avoid acne
					if(intersection.distance < ray.origin.Subtract(position).getMagnitude()) { // if point light is nearer than intersection - no problemo
						return true;
					}
				}
			}
		}
    	return false;
	}

	@Override
    public Color illuminate(Ray ray, Intersection intersection){ // All the same as in parallel light, just that it calculates the direction for each intersection
		Vector direction = intersection.intersectionPoint.Subtract(position);
		direction = direction.normalize();

		Color iC = intersection.colorAtIntersection; // changed to COLOR FROM INTERSECTION!
		float kd = intersection.material.phong.kd;
		float ks = intersection.material.phong.ks;
		float exponent = intersection.material.phong.exponent;

		// Diffuse
		float diffuse = (float) -Math.min(0.0, direction.normalize().dot(intersection.normal.normalize())); // Minus (which becomes a postive number) because of reversing one vector (if Normal and Light to same direction, then no illumination)
		
		// Diffuse Coefficient * Diffuse Part * Intersection Color * Light Color
		float rd = kd * diffuse * iC.r * this.color.r;
		float gd = kd * diffuse * iC.g * this.color.g;
		float bd = kd * diffuse * iC.b * this.color.b;
		Color c = new Color(rd, gd, bd);
		
		// Specular
		Vector viewVector = ray.direction.Scale(-1).normalize();
		Vector lightVector = direction.Scale(-1).normalize();
		Vector normal = intersection.normal.normalize();
		Vector reflectionVector = normal.Scale( normal.dot(lightVector) ) . Scale(2) . Subtract(lightVector) . normalize(); // r = 2(n ⋅ l)n – l
		float specular = (float) Math.pow(Math.max(0, reflectionVector.dot(viewVector)), exponent);
		
		float rs = ks * specular * this.color.r;
		float gs = ks * specular * this.color.g;
		float bs = ks * specular * this.color.b;
		c.addColor(new Color(rs, gs, bs));
		
		return c;
    }
	
}

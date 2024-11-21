package surfaces;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;

import model.Color;
import model.Intersection;
import model.OBJ;
import model.Ray;
import model.Vector;

public class Mesh extends Surface{
	
	public String objName;
	public OBJ meshObj; 
	
	public Mesh(boolean solidMaterial) {
		super(solidMaterial);
		meshObj = new OBJ();
	}
	
	// Taken from and written to java: https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/moller-trumbore-ray-triangle-intersection.html
	@Override
	public Intersection intersect(Ray ray) {
		for (int i = 0; i < meshObj.faceVerticesIndices.size(); i++) {
			// Read vertices with the vertexindices that are provided in the faces
			Vector vertex0 = new Vector(
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[0])[0],
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[0])[1],
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[0])[2]);
			Vector vertex1 = new Vector(
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[1])[0],
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[1])[1],
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[1])[2]);
			Vector vertex2 = new Vector(
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[2])[0],
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[2])[1],
					meshObj.vertices.get(meshObj.faceVerticesIndices.get(i)[2])[2]);
			
			
			Vector v0v1 = vertex1.Subtract(vertex0);
			Vector v0v2 = vertex2.Subtract(vertex0);
			Vector pvec = ray.direction.cross(ray.direction, v0v2);
	        float det = v0v1.dot(pvec);
	        
	        float invDet = 1 / det;

	        Vector tvec = ray.origin.Subtract(vertex0);
	        float a = tvec.dot(pvec) * invDet;
	        if (a < 0 || a > 1) continue;

	        Vector qvec = tvec.cross(tvec, v0v1);
	        float b = ray.direction.dot(qvec) * invDet;
	        if (b < 0 || a + b > 1) continue;

	        
	        float t = v0v2.dot(qvec) * invDet;
	        
	        
			Vector intersectionPoint = ray.direction.Scale(t).Add(ray.origin);
			Vector normal = new Vector(
					meshObj.normals.get(meshObj.faceNormalsIndices.get(i)[0])[0], // in this case, all Vertices of one triangle look into the same direction, all Normals are the same
					meshObj.normals.get(meshObj.faceNormalsIndices.get(i)[0])[1], // so i just take the index for the Normal of the first Vertex ([0])
					meshObj.normals.get(meshObj.faceNormalsIndices.get(i)[0])[2]);
			normal = normal.normalize();

			Intersection intersection = null;
			
			if(!material.solid) { // textured
				float u1 = meshObj.textures.get(meshObj.faceTexturesIndices.get(i)[0])[0];
				float v1 = meshObj.textures.get(meshObj.faceTexturesIndices.get(i)[0])[1];
	
				float u2 = meshObj.textures.get(meshObj.faceTexturesIndices.get(i)[1])[0];
				float v2 = meshObj.textures.get(meshObj.faceTexturesIndices.get(i)[1])[1];
	
				float u3 = meshObj.textures.get(meshObj.faceTexturesIndices.get(i)[2])[0];
				float v3 = meshObj.textures.get(meshObj.faceTexturesIndices.get(i)[2])[1];
				
				float u = (1 - a - b)*u1 + a*u2 + b*u3;
				float v = (1 - a - b)*v1 + a*v2 + b*v3;
				
				if(u > 1) u -= 1;
				if(v > 1) v -= 1;
				
				int x = (int) ((float)(material.textureWidth-1) * u);
				int y = (int) ((float)(material.textureHeight-1) * v);
				
				Color colorAtIntersection = material.texture[x][y]; // and then it worked
		        intersection = new Intersection(colorAtIntersection, t, intersectionPoint, normal, material);
			}
			else {
		        intersection = new Intersection(t, intersectionPoint, normal, material);
			}
			
	        return intersection;
		}
		
		return null;
	}

}

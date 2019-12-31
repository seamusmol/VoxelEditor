package worldGen;

public class Cell
{
    public int value;
    public int px;
    public int py;
    public int pz;
    public int[] m;
    public boolean rot;
    
    public Cell(int Value, int PX, int PY, int PZ, int[] M)
    {
        value = Value;
        px = PX;
        py = PY;
        pz = PZ;
        m = M;
        rot = false;
    }
    
    public Cell(int Value, int PX, int PY, int PZ, int[] M, boolean Rot)
    {
        value = Value;
        px = PX;
        py = PY;
        pz = PZ;
        m = M;
        rot = Rot;
    }
    
}

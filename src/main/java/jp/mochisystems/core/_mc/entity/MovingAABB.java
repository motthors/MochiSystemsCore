package jp.mochisystems.core._mc.entity;

import jp.mochisystems.core._mc._core.Logger;
import jp.mochisystems.core._mc.eventhandler.TickEventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

public class MovingAABB extends AxisAlignedBB {

    EntityCollisionParts parent;

    public MovingAABB(EntityCollisionParts parent, double mx, double my, double mz, double xx, double xy, double xz) {
        super(mx, my, mz, xx, xy, xz);
        this.parent = parent;
    }

    @Override
    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2)
    {
//        double diffX = parent.posX - parent.prevPosX;
//        double diffY = parent.posY - parent.prevPosY;
//        double diffZ = parent.posZ - parent.prevPosZ;
//        return super.intersects(
//                x1+diffX,
//                y1+diffY,
//                z1+diffZ,
//                x2+diffX,
//                y2+diffY,
//                z2+diffZ);
        return super.intersects(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public double calculateYOffset(AxisAlignedBB other, double offsetY)
    {
        double diff = parent.connector.Current().y - parent.connector.Prev().y;
        double d = diff >= 0 ? diff : 0;

        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetY > 0.0D && other.maxY <= this.minY)
            {
                diff *= (diff <= 0) ? 1.02 : 0.99;
                double d1 = this.minY - other.maxY + diff;

                if (d1 < offsetY)
                {
                    offsetY = d1;
                }
            }
            else if (offsetY <= 0.0D && other.minY >= this.maxY)
            {
                diff *= (diff >= 0) ? 1.02 : 0.999;
                double d0 = this.maxY - other.minY + diff;

                if (d0 > offsetY)
                {
                    offsetY = d0;
                }
            }
        }
        return offsetY;
    }

    @Override
    public double calculateXOffset(AxisAlignedBB other, double offsetX)
    {
        double diff = parent.posX - parent.prevPosX;

        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetX > 0.0D && other.maxX <= this.minX)
            {
                if(diff < -0.001) diff *= 1.02;
                else if(diff > 0.001) diff *= 0.99;
                double d1 = this.minX - other.maxX + diff;

                if (d1 < offsetX)
                {
                    offsetX = d1;
                }
            }
            else if (offsetX < 0.0D && other.minX >= this.maxX)
            {
                if(diff > 0.001) diff *= 1.02;
                else if(diff < -0.001) diff *= 0.99;
                double d0 = this.maxX - other.minX + diff;

                if (d0 > offsetX)
                {
                    offsetX = d0;
                }
            }

        }
        return offsetX;
    }

    @Override
    public double calculateZOffset(AxisAlignedBB other, double offsetZ)
    {
        double diff = parent.posX - parent.prevPosX;

        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY)
        {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ)
            {
                if(diff < -0.001) diff *= 1.02;
                else if(diff > 0.001) diff *= 0.99;
                double d1 = this.minZ - other.maxZ + diff;

                if (d1 < offsetZ)
                {
                    offsetZ = d1;
                }
            }
            else if (offsetZ < 0.0D && other.minZ >= this.maxZ)
            {
                if(diff > 0.001) diff *= 1.02;
                else if(diff < -0.001) diff *= 0.99;
                double d0 = this.maxZ - other.minZ + diff;

                if (d0 > offsetZ)
                {
                    offsetZ = d0;
                }
            }

        }
        return offsetZ;
    }




    @Nonnull
    @Override
    public AxisAlignedBB contract(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D) d0 -= x;
        else if (x > 0.0D) d3 -= x;

        if (y < 0.0D) d1 -= y;
        else if (y > 0.0D) d4 -= y;

        if (z < 0.0D) d2 -= z;
        else if (z > 0.0D) d5 -= z;
        return new MovingAABB(parent, d0, d1, d2, d3, d4, d5);
    }

    @Nonnull
    @Override
    public AxisAlignedBB expand(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
            d0 += x;
        else if (x > 0.0D)
            d3 += x;

        if (y < 0.0D)
            d1 += y;
        else if (y > 0.0D)
            d4 += y;

        if (z < 0.0D)
            d2 += z;
        else if (z > 0.0D)
            d5 += z;

        return new MovingAABB(parent, d0, d1, d2, d3, d4, d5);
    }

    @Override
    public AxisAlignedBB grow(double x, double y, double z)
    {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new MovingAABB(parent, d0, d1, d2, d3, d4, d5);
    }

    @Override
    public AxisAlignedBB intersect(AxisAlignedBB other)
    {
        double d0 = Math.max(this.minX, other.minX);
        double d1 = Math.max(this.minY, other.minY);
        double d2 = Math.max(this.minZ, other.minZ);
        double d3 = Math.min(this.maxX, other.maxX);
        double d4 = Math.min(this.maxY, other.maxY);
        double d5 = Math.min(this.maxZ, other.maxZ);
        return new MovingAABB(parent, d0, d1, d2, d3, d4, d5);
    }

    @Override
    public AxisAlignedBB union(AxisAlignedBB other)
    {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return new MovingAABB(parent, d0, d1, d2, d3, d4, d5);
    }

    @Override
    public AxisAlignedBB offset(double x, double y, double z)
    {
        return new MovingAABB(parent, this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    @Override
    public AxisAlignedBB offset(BlockPos pos)
    {
        return new MovingAABB(parent,this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.minZ + (double)pos.getZ(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY(), this.maxZ + (double)pos.getZ());
    }
}

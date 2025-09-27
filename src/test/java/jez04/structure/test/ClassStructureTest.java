package jez04.structure.test;

import cz.vsb.fei.kelvin.unittest.ClassExist;
import cz.vsb.fei.kelvin.unittest.HasConstructor;
import cz.vsb.fei.kelvin.unittest.HasMethod;
import cz.vsb.fei.kelvin.unittest.StructureHelper;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

class ClassStructureTest {
    StructureHelper helper = StructureHelper.getInstance(ClassStructureTest.class);

    @Test
    void testCannonClassExists() {
        assertThat(ClassStructureTest.class, new ClassExist("Cannon"));
    }
    @Test
    void testCannonMethods() throws ClassNotFoundException {
        Class<?> cannon = helper.getClass("Cannon");
        assertThat(cannon, new HasMethod("draw", void.class, GraphicsContext.class));
        assertThat(cannon, new HasMethod("simulate", void.class, double.class));
    }

    @Test
    void testWorldClassExists() {
        assertThat(ClassStructureTest.class, new ClassExist("World"));
    }

    @Test
    void testWorldMethods() throws ClassNotFoundException {
        Class<?> world = helper.getClass("World");
        assertThat(world, new HasMethod("draw", void.class, GraphicsContext.class));
        assertThat(world, new HasMethod("simulate", void.class, double.class));
    }

    @Test
    void testBulletFileds() throws ClassNotFoundException {
        Class<?> bullet = helper.getClass("Bullet");
        assertThat(bullet, new HasConstructor(Point2D.class, Point2D.class, Point2D.class));
        assertThat(bullet, new HasConstructor());
    }

    @Test
    void testBulletClassExists() {
        assertThat(ClassStructureTest.class, new ClassExist("Bullet"));
    }

    @Test
    void testBulletMethods() throws ClassNotFoundException {
        Class<?> world = helper.getClass("Bullet");
        assertThat(world, new HasMethod("draw", void.class, GraphicsContext.class));
        assertThat(world, new HasMethod("simulate", void.class, double.class));
    }
    @Test
    void testBulletAnimatedFileds() throws ClassNotFoundException {
        Class<?> bullet = helper.getClass("BulletAnimated");
        assertThat(bullet, new HasConstructor(Point2D.class, Point2D.class, Point2D.class));
    }

    @Test
    void testBulletAnimatedClassExists() {
        assertThat(ClassStructureTest.class, new ClassExist("BulletAnimated"));
    }

    @Test
    void testBulletAnimatedMethods() throws ClassNotFoundException {
        Class<?> world = helper.getClass("BulletAnimated");
        assertThat(world, new HasMethod("draw", void.class, GraphicsContext.class));
        assertThat(world, new HasMethod("simulate", void.class, double.class));
    }

}

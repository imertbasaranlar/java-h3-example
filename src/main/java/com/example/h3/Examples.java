package com.example.h3;

import com.uber.h3core.AreaUnit;
import com.uber.h3core.H3Core;
import com.uber.h3core.LengthUnit;
import com.uber.h3core.util.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class Examples {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    
    private static final H3Core h3;

    static {
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        double locationLat = 40.971324693596145;
        double locationLng = 29.08740265946784;
        int resolution = 7;

        long locationCell = h3.latLngToCell(locationLat, locationLng, resolution);
        log.info("Location cell : " + locationCell);
        
        boolean checkCellIsValid = h3.isValidCell(locationCell);
        log.info("Check location cell is valid : " + checkCellIsValid);

        int cellResolution = h3.getResolution(locationCell);
        log.info("Location cell resolution : " + cellResolution);

        double locationCellAreaAsKm = h3.cellArea(locationCell, AreaUnit.km2);
        log.info("Location cell area as km : " + locationCellAreaAsKm);

        List<Long> originToDirectedEdges = h3.originToDirectedEdges(locationCell);
        log.info("Location cell origin to directed edges : " + originToDirectedEdges);

        double edgeLength = h3.edgeLength(originToDirectedEdges.get(0), LengthUnit.m);
        log.info("Location cell edges length : " + edgeLength);

        LatLng locationLatLng = h3.cellToLatLng(locationCell);
        log.info("Location cell lat lng : " + locationLatLng);

        List<LatLng> locationCellToBoundary = h3.cellToBoundary(locationCell);
        log.info("Location cell boundaries : " + locationCellToBoundary);

        // polygonToCells
        List<Long> polygonToCells = h3.polygonToCells(List.of(
                        new LatLng(40.97327275670735, 29.08929718462629),
                        new LatLng(40.96895076850403, 29.097167934893424),
                        new LatLng(40.96772615370788, 29.08920178159275),
                        new LatLng(40.97147196269585, 29.086864407271005)
                ), List.of(List.of(new LatLng(40.971183831092674, 29.090298916478478))), 9);
        log.info("Polygon to cells : " + polygonToCells);
        List<Polygon> polygons = polygonToCells.stream().map(Examples::toPolygon).toList();
        log.info("Polygons : " + polygons);
        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[0]));
        log.info("Multipolygon : " + multiPolygon);

        long grandParentCell = h3.cellToParent(locationCell, 5);
        log.info("Grand parent cell : " + grandParentCell);

        List<Long> childrenCells = h3.cellToChildren(locationCell, 9);
        log.info("Children cells : " + childrenCells);

        long location1Cell = h3.latLngToCell(40.96235919141393, 29.09940990618163, 8);
        long location2Cell = h3.latLngToCell(40.97309267951806, 29.09130064833066, 8);
        long gridDistance1to2 = h3.gridDistance(location1Cell, location2Cell);
        log.info("Grid distance location 1 to 2 : " + gridDistance1to2);
        long gridDistance2to1 = h3.gridDistance(location2Cell, location1Cell);
        log.info("Grid distance location 2 to 1 : " + gridDistance2to1);

        List<Long> gridPathCells = h3.gridPathCells(location1Cell, location2Cell);
        log.info("Grid path cells for location1 and location2 : " + gridPathCells);

    }

    public static Point createPoint(Double lng, Double lat) {
        return new Point(new CoordinateArraySequence(new Coordinate[]{
                new Coordinate(Objects.isNull(lng) ? 0.0 : lng, Objects.isNull(lat) ? 0.0 : lat)
        }), geometryFactory);
    }

    public static Polygon toPolygon(List<Point> points) {
        return geometryFactory.createPolygon(points.stream().map(Point::getCoordinate).toList().toArray(Coordinate[]::new));
    }

    public static Polygon toPolygon(long cell) {
        List<LatLng> points = h3.cellToBoundary(cell);
        points.add(points.get(0));
        return toPolygon(points.stream()
                .map(ltlng -> createPoint(ltlng.lng, ltlng.lat))
                .toList());
    }
}

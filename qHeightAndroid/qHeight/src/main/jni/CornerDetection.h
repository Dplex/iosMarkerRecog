/*
 * CCornerDetection.h
 *
 *  Created on: 2016. 1. 17.
 *      Author: Windows
 */

#ifndef JNI_CornerDetection_H_
#define JNI_CornerDetection_H_

#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <math.h>
#include <vector>

using namespace cv;

class CCornerDetection {

public:
  CCornerDetection();
  ~CCornerDetection();

  void setParam(std::vector<cv::Point> contours);
  void detectCorner();

  CvPoint corner[4];

private:
  void _extractCorner();
  void _setCorner();
  std::vector<cv::Point> m_contours;
  int m_pointCount;
  CvPoint m_point[4];
};

CCornerDetection::CCornerDetection() {
  m_pointCount = 0;
}

CCornerDetection::~CCornerDetection() {

}

void CCornerDetection::setParam(std::vector<cv::Point> contours) {
  m_contours = contours;
}

void CCornerDetection::detectCorner() {
  _extractCorner();
  _setCorner();
}

void CCornerDetection::_setCorner() {
  double minX = 0.0;
  double minY = 0.0;
  for (int i = 0; i < 4; i++) {
    minX += m_point[i].x;
    minY += m_point[i].y;
  }
  minX /= 4;
  minY /= 4;

  for (int i = 0; i < 4; i++) {
    if (m_point[i].x < minX && m_point[i].y < minY) {
      corner[0] = m_point[i];
    }
    if (m_point[i].x >= minX && m_point[i].y < minY) {
      corner[1] = m_point[i];
    }
    if (m_point[i].x >= minX && m_point[i].y >= minY) {
      corner[2] = m_point[i];
    }
    if (m_point[i].x < minX && m_point[i].y >= minY) {
      corner[3] = m_point[i];
    }
  }
}

double _getDistance(CvPoint p1, CvPoint p2) {
  double d = sqrt(
      (double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
  return d;
}

void CCornerDetection::_extractCorner() {

  //p1
  double maxDist = 0.0;
  CvPoint origin = cvPoint(0, 0);
  for (size_t i = 0; i < m_contours.size(); i++) {
    CvPoint p = m_contours[i];
    double dist = _getDistance(p, origin);
    if (maxDist < dist) {
      maxDist = dist;
      m_point[0].x = p.x;
      m_point[0].y = p.y;
    }
  }

  //p2
  maxDist = 0.0;
  for (size_t i = 0; i < m_contours.size(); i++) {
    CvPoint p = m_contours[i];
    double dist = _getDistance(p, m_point[0]);
    if (maxDist < dist) {
      maxDist = dist;
      m_point[1].x = p.x;
      m_point[1].y = p.y;
    }
  }

  //p3
  maxDist = 0.0;
  for (size_t i = 0; i < m_contours.size(); i++) {
    CvPoint p = m_contours[i];

    double dist1 = _getDistance(p, m_point[0]);
    double dist2 = _getDistance(p, m_point[1]);
    double dist = dist1 + dist2;
    if (maxDist < dist) {
      maxDist = dist;
      m_point[2].x = p.x;
      m_point[2].y = p.y;
    }
  }

  //p4 gauss's function (find p4 point that maximize rectagle)

  int x1 = m_point[0].x;
  int y1 = m_point[0].y;

  int x2 = m_point[1].x;
  int y2 = m_point[1].y;

  int x3 = m_point[2].x;
  int y3 = m_point[2].y;

  int nMaxDim = 0;

  for (size_t i = 0; i < m_contours.size(); i++) {

    CvPoint p = m_contours[i];

    int nDim = abs(
        (x1 * y2 + x2 * p.y + p.x * y1) - (x2 * y1 + p.x * y2 + x1 * p.y))
        + abs((x1 * p.y + p.x * y3 + x3 * y1) - (p.x * y1 + x3 * p.y + x1 * y3))
        + abs(
            (p.x * y2 + x2 * y3 + x3 * p.y) - (x2 * p.y + x3 * y2 + p.x * y3));

    if (nMaxDim < nDim) {
      nMaxDim = nDim;
      m_point[3].x = p.x;
      m_point[3].y = p.y;
    }
  }

}

#endif /* JNI_CornerDetection_H_ */

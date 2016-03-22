#pragma once
#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/opencv.hpp>
#include <math.h>
#include <vector>
#include <android/log.h>

typedef std::vector<cv::Point> CONTOUR;
#define PERCENT 95

class CMarkerRcgn {

public:

  CvPoint m_point[4];
  std::vector<CONTOUR> m_markerCandidates;
  cv::Mat m_img; // frame
  cv::Mat m_marker; // maker image

  void setParam(cv::Mat, cv::Mat); // parameter setting
  void doProc(); // processing

  CMarkerRcgn();
  ~CMarkerRcgn();

protected:

private:

  void _preProcAndWarping(cv::Mat&, cv::Mat&, CONTOUR&);
  void _preProc(cv::Mat&, cv::Mat&);
  //warping : http://arnab.org/blog/so-i-suck-24-automating-card-games-using-opencv-and-python
  void _warping(cv::Mat&, cv::Mat&, CvPoint Cor[]);

  bool _checkRect(CONTOUR&);
  bool _checkInRect(CONTOUR&);
  bool _isMarkerExist(cv::Mat&, CONTOUR&);

  void _detectMarkerCandidates(cv::Mat&, std::vector<CONTOUR>&);
  bool _isSquare(std::vector<cv::Point>&);
  double _getAngle(cv::Point, cv::Point, cv::Point);

  int m_width;
  int m_height;

};

CMarkerRcgn::CMarkerRcgn() {
}

CMarkerRcgn::~CMarkerRcgn() {
}

void CMarkerRcgn::setParam(cv::Mat src, cv::Mat mkr) {
  m_img = src.clone();
  m_marker = mkr.clone();

  m_width = src.cols;
  m_height = src.rows;
}

double CMarkerRcgn::_getAngle(cv::Point pt1, cv::Point pt2, cv::Point pt0) {
  double dx1 = pt1.x - pt0.x;
  double dy1 = pt1.y - pt0.y;
  double dx2 = pt2.x - pt0.x;
  double dy2 = pt2.y - pt0.y;
  double a = dx1 * dx2 + dy1 * dy2;
  double b = sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
  return a / b;
}

void CMarkerRcgn::_preProc(cv::Mat& colorImg, cv::Mat& dst) {

  // RGB_Color to Gray
  cv::cvtColor(colorImg, dst, CV_BGR2GRAY);

  // noise removal, img soft 
  cv::blur(dst, dst, cv::Size(3, 3));

  // close calculation
  cv::Mat element(5, 5, CV_8U, cv::Scalar(1));
  cv::morphologyEx(dst, dst, cv::MORPH_CLOSE, element);

  // Gray to Binary
  cv::adaptiveThreshold(dst, dst, 255, CV_ADAPTIVE_THRESH_GAUSSIAN_C,
      CV_THRESH_BINARY_INV, 51, 15);

}

void CMarkerRcgn::doProc() {

  // image preprocessing
  cv::Mat binaryImage = cv::Mat::zeros(m_img.size(), CV_8UC1);
  _preProc(m_img, binaryImage);

  // mark detection
  m_markerCandidates.clear();
  _detectMarkerCandidates(binaryImage, m_markerCandidates);

  if (m_markerCandidates.size() > 0) {
    CCornerDetection vec;
    vec.setParam(m_markerCandidates[0]);
    vec.detectCorner();
    for (int i = 0; i < 4; i++) {
      m_point[i] = vec.corner[i];
    }
  }
}

bool CMarkerRcgn::_isSquare(std::vector<cv::Point>& contours) {
  CCornerDetection vec;
  vec.setParam(contours);
  vec.detectCorner();
  double x = 0, y = 0;
  double v_size = contours.size();
  for (size_t i = 0; i < contours.size(); i++) {
    x += contours[i].x;
    y += contours[i].y;
  }

  double Hrzt_lenX = vec.corner[0].x - vec.corner[1].x;
  double Hrzt_lenY = vec.corner[0].y - vec.corner[1].y;

  double Hrzt_len = sqrt((Hrzt_lenX * Hrzt_lenX) + (Hrzt_lenY * Hrzt_lenY));

  double Vrtx_lenX = vec.corner[1].x - vec.corner[2].x;
  double Vrtx_lenY = vec.corner[1].y - vec.corner[2].y;

  double Vrtx_len = sqrt((Vrtx_lenX * Vrtx_lenX) + (Vrtx_lenY * Vrtx_lenY));

  double h_ratio = Hrzt_len / (Vrtx_len + Hrzt_len + 0.001);
  double v_ratio = Vrtx_len / (Vrtx_len + Hrzt_len + 0.001);

  if (fabs(h_ratio - v_ratio) > 0.2)
    return false;

  x /= v_size + 0.001;
  y /= v_size + 0.001;

  int cnr_x = 0, cnr_y = 0;
  for (int i = 0; i < 4; i++) {
    cnr_x += vec.corner[i].x;
    cnr_y += vec.corner[i].y;
  }

  cnr_x /= 4;
  cnr_y /= 4;
  if (abs(x - cnr_x) < 3 && abs(y - cnr_y) < 3) {
    return true;
  }
  return false;
}

bool CMarkerRcgn::_checkRect(CONTOUR& approx) {
  if (approx.size() == 4 && cv::contourArea(cv::Mat(approx)) > 100.f
      && cv::isContourConvex(cv::Mat(approx))) {
    double maxCosine = 0;
    for (int j = 2; j < 5; j++) {
      double cosine = fabs(
          _getAngle(approx[j % 4], approx[j - 2], approx[j - 1]));
      maxCosine = MAX(maxCosine, cosine);
    }
    if (maxCosine < 0.3 && _isSquare(approx)) {
      return true;
    }
  }
  return false;
}

void CMarkerRcgn::_detectMarkerCandidates(cv::Mat& binaryImage,
    std::vector<CONTOUR>& markerCandidates) {

  // maker detection
  std::vector<CONTOUR> contours;
  cv::findContours(binaryImage, contours, CV_RETR_EXTERNAL,
      CV_CHAIN_APPROX_NONE);

  CONTOUR approx;
  for (size_t i = 0; i < contours.size(); i++) {
    double kMargin = 0.02;
    cv::approxPolyDP(cv::Mat(contours[i]), approx,
        arcLength(cv::Mat(contours[i]), true) * kMargin, true);
    if (_checkRect(approx) && _checkInRect(approx)) {
      markerCandidates.push_back(approx);
    }
  }
}

void CMarkerRcgn::_warping(cv::Mat& src, cv::Mat& dst, CvPoint Cor[]) {

  cv::Point2f src_vertices[4];
  src_vertices[0] = Cor[0];
  src_vertices[1] = Cor[1];
  src_vertices[2] = Cor[2];
  src_vertices[3] = Cor[3];

  cv::Point2f dst_vertices[4];
  dst_vertices[0] = cv::Point(0, 0);
  dst_vertices[1] = cv::Point(dst.cols, 0);
  dst_vertices[2] = cv::Point(dst.cols, dst.rows);
  dst_vertices[3] = cv::Point(0, dst.rows);

  cv::Mat warpMatrix = getPerspectiveTransform(src_vertices, dst_vertices);

  cv::warpPerspective(src, dst, warpMatrix, dst.size(), cv::INTER_LINEAR,
      cv::BORDER_CONSTANT);

}

void CMarkerRcgn::_preProcAndWarping(cv::Mat& src, cv::Mat& dst,
    CONTOUR& rt_approx) {

  CCornerDetection vec;
  vec.setParam(rt_approx);
  vec.detectCorner();
  _warping(src, dst, vec.corner);
  cv::cvtColor(dst, dst, CV_RGB2GRAY);
  cv::threshold(dst, dst, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);

}

bool CMarkerRcgn::_checkInRect(CONTOUR& rt_approx) {

  cv::Mat dst = cv::Mat::zeros(m_marker.size(), CV_8UC3);
  _preProcAndWarping(m_img, dst, rt_approx);
  cv::Mat color_dst;
  cv::cvtColor(dst, color_dst, CV_GRAY2BGR);
  // close calculation
  cv::Mat element(5, 5, CV_8U, cv::Scalar(1));
  cv::morphologyEx(dst, dst, cv::MORPH_CLOSE, element);

  std::vector<CONTOUR> contours;
  cv::findContours(dst, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE);

  CONTOUR approx;
  std::vector<CONTOUR> cc;
  for (size_t i = 0; i < contours.size(); i++) {
    cv::approxPolyDP(cv::Mat(contours[i]), approx,
        arcLength(cv::Mat(contours[i]), true) * 0.02, true);
    if (_checkRect(approx) && _isMarkerExist(color_dst, approx))
      cc.push_back(approx);
  }
  if (cc.size() == 1) {
    return true;
  }
  return false;

}

bool CMarkerRcgn::_isMarkerExist(cv::Mat& src, CONTOUR& rt_approx) {

  cv::Mat mrkBinary;
  cv::cvtColor(m_marker, mrkBinary, CV_BGR2GRAY);
  cv::threshold(mrkBinary, mrkBinary, 0, 255,
      CV_THRESH_BINARY | CV_THRESH_OTSU);

  cv::Mat dst = cv::Mat::zeros(m_marker.size(), CV_8UC3);
  _preProcAndWarping(src, dst, rt_approx);

  IplImage* origin = new IplImage(dst);
  IplImage* marker = new IplImage(mrkBinary);

  for (int r = 0; r < 4; r++) {
    //1. rotation
    int cnt = 0;
    CvPoint2D32f center = cvPoint2D32f(marker->width / 2.0,
        marker->height / 2.0);
    CvMat *rot_mat = cvCreateMat(2, 3, CV_32FC1);
    cv2DRotationMatrix(center, 90, 1, rot_mat);
    cvWarpAffine(marker, marker, rot_mat,
        CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);

    //2. matching
    for (int i = 0; i < origin->height; i++) {
      for (int j = 0; j < origin->width; j++) {
        if ((unsigned char) origin->imageData[i * origin->widthStep + j]
            == (unsigned char) marker->imageData[i * marker->widthStep + j])
          cnt++;
      }
    }

    //3. check
    __android_log_print(ANDROID_LOG_DEBUG, "INITSVM", "%d", cnt);
    if (cnt > (m_marker.rows * m_marker.cols) / 100 * PERCENT) {
      return true;
    }
  }
  return false;
}


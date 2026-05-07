package hu.kdea.szavazas

import org.opencv.core.*

object ZhangSuenThinning {
    fun apply(binary: Mat): Mat {
        var img = binary.clone()
        img.convertTo(img, CvType.CV_8UC1)
        var changed = true
        while (changed) {
            changed = false
            val marker = Mat.zeros(img.size(), CvType.CV_8UC1)
            // Step 1
            for (y in 1 until img.rows()-1) {
                for (x in 1 until img.cols()-1) {
                    if (img.get(y, x)[0] == 255.0) {
                        val neigh = neighbourCount(img, x, y)
                        val trans = transitions(img, x, y)
                        if (neigh in 2..6 && trans == 1) {
                            val p2 = img.get(y-1, x)[0] == 255.0
                            val p4 = img.get(y, x+1)[0] == 255.0
                            val p6 = img.get(y+1, x)[0] == 255.0
                            val p8 = img.get(y, x-1)[0] == 255.0
                            if ((p2 && p4 && p6) || (p4 && p6 && p8))
                                marker.put(y, x, 255.0)
                        }
                    }
                }
            }
            changed = applyMarker(img, marker)
            marker.setTo(Scalar(0.0))
            // Step 2
            for (y in 1 until img.rows()-1) {
                for (x in 1 until img.cols()-1) {
                    if (img.get(y, x)[0] == 255.0) {
                        val neigh = neighbourCount(img, x, y)
                        val trans = transitions(img, x, y)
                        if (neigh in 2..6 && trans == 1) {
                            val p2 = img.get(y-1, x)[0] == 255.0
                            val p4 = img.get(y, x+1)[0] == 255.0
                            val p6 = img.get(y+1, x)[0] == 255.0
                            val p8 = img.get(y, x-1)[0] == 255.0
                            if ((p2 && p4 && p8) || (p2 && p6 && p8))
                                marker.put(y, x, 255.0)
                        }
                    }
                }
            }
            changed = changed or applyMarker(img, marker)
        }
        return img
    }

    private fun neighbourCount(img: Mat, x: Int, y: Int): Int {
        var cnt = 0
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                if (img.get(y+dy, x+dx)[0] == 255.0) cnt++
            }
        }
        return cnt
    }

    private fun transitions(img: Mat, x: Int, y: Int): Int {
        val order = listOf(Pair(0,-1), Pair(1,-1), Pair(1,0), Pair(1,1),
            Pair(0,1), Pair(-1,1), Pair(-1,0), Pair(-1,-1))
        val n = IntArray(9)
        for (i in 0 until 8) {
            n[i] = if (img.get(y+order[i].second, x+order[i].first)[0] == 255.0) 1 else 0
        }
        n[8] = n[0]
        var trans = 0
        for (i in 0 until 8) if (n[i]==0 && n[i+1]==1) trans++
        return trans
    }

    private fun applyMarker(img: Mat, marker: Mat): Boolean {
        var changed = false
        for (y in 0 until img.rows()) {
            for (x in 0 until img.cols()) {
                if (marker.get(y, x)[0] == 255.0) {
                    img.put(y, x, 0.0)
                    changed = true
                }
            }
        }
        return changed
    }
}
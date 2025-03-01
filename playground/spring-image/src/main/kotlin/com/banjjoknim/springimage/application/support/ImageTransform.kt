package com.banjjoknim.springimage.application.support

import java.awt.geom.AffineTransform

data class ImageTransform(
    val transform: AffineTransform,
    val transformWidth: Int,
    val transformHeight: Int,
)

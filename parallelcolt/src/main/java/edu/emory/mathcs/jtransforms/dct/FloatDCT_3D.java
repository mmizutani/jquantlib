/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JTransforms.
 *
 * The Initial Developer of the Original Code is
 * Piotr Wendykier, Emory University.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package edu.emory.mathcs.jtransforms.dct;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cern.colt.Utils;

/**
 * Computes 3D Discrete Cosine Transform (DCT) of single precision data where
 * all three dimensions are integers power of 2. This is a parallel
 * implementation of split-radix algorithm optimized for SMP systems. <br>
 * <br>
 * This code is derived from General Purpose FFT Package written by Takuya Ooura
 * (http://www.kurims.kyoto-u.ac.jp/~ooura/fft.html)
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * @version 1.1 01/01/2008
 * 
 */
public class FloatDCT_3D {

	private static final int DCT3D_THREADS_BEGIN_N = 65536;

	private int n1;

	private int n2;

	private int n3;

	private int sliceStride;

	private int rowStride;

	// private int colStride=1;

	private int[] ip;

	private float[] w;

	private float[] t;

	private int nt;

	private FloatDCT_1D dctn1, dctn2, dctn3;

	private int np;

	/**
	 * Creates new instance of FloatDCT_3D.
	 * 
	 * @param n1
	 *            slice dimension - must be power of 2
	 * @param n2
	 *            row dimension - must be power of 2
	 * @param n3
	 *            column dimension - must be power of 2
	 */
	public FloatDCT_3D(int n1, int n2, int n3) {
		if (!Utils.isPowerOf2(n1) || !Utils.isPowerOf2(n2) || !Utils.isPowerOf2(n3))
			throw new IllegalArgumentException("All dimensions must be power of two");
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
		this.sliceStride = n2 * n3;
		this.rowStride = n3;

		ip = new int[2 + (int) Math.ceil(Math.sqrt(Math.max(Math.max(n1 / 2, n2 / 2), n3 / 2)))];
		w = new float[(int) Math.ceil(Math.max(Math.max(n1 * 1.5, n2 * 1.5), n3 * 1.5))];
		dctn1 = new FloatDCT_1D(n1, ip, w);
		dctn2 = new FloatDCT_1D(n2, ip, w);
		dctn3 = new FloatDCT_1D(n3, ip, w);
		this.np = Utils.getNP();
		if (np > 1) {
			nt = (Math.max(4 * n1, 4 * n2)) * np;

		} else {
			nt = Math.max(4 * n1, 4 * n2);
		}
		t = new float[nt];
	}

	/**
	 * Computes the 3D forward DCT (DCT-II) leaving the result in <code>a</code>.
	 * The data is stored in 1D array addressed in slice-major, then row-major,
	 * then column-major, in order of significance, i.e. the element (i,j,k) of
	 * 3D array x[n1][n2][n3] is stored in a[i*sliceStride + j*rowStride + k],
	 * where sliceStride = n2 * n3 and rowStride = n3.
	 * 
	 * @param a
	 *            data to transform
	 * @param scale
	 *            if true then scaling is performed
	 */
	public void forward(float[] a, boolean scale) {
		int n, nw, nc;

		n = n1;
		if (n < n2) {
			n = n2;
		}
		if (n < n3) {
			n = n3;
		}
		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw);
		}
		nc = ip[1];
		if (n > nc) {
			nc = n;
			makect(nc, w, nw);
		}
		t = new float[nt];
		if ((np > 1) && (n1 * n2 * n3 >= DCT3D_THREADS_BEGIN_N)) {
			ddxt3da_subth(-1, a, scale);
			ddxt3db_subth(-1, a, scale);
		} else {
			ddxt3da_sub(-1, a, scale);
			ddxt3db_sub(-1, a, scale);
		}
		t = null;

	}

	/**
	 * Computes the 3D forward DCT (DCT-II) leaving the result in <code>a</code>.
	 * The data is stored in 3D array
	 * 
	 * @param a
	 *            data to transform
	 * @param scale
	 *            if true then scaling is performed
	 */
	public void forward(float[][][] a, boolean scale) {
		int n, nw, nc;

		n = n1;
		if (n < n2) {
			n = n2;
		}
		if (n < n3) {
			n = n3;
		}
		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw);
		}
		nc = ip[1];
		if (n > nc) {
			nc = n;
			makect(nc, w, nw);
		}
		t = new float[nt];
		if ((np > 1) && (n1 * n2 * n3 >= DCT3D_THREADS_BEGIN_N)) {
			ddxt3da_subth(-1, a, scale);
			ddxt3db_subth(-1, a, scale);
		} else {
			ddxt3da_sub(-1, a, scale);
			ddxt3db_sub(-1, a, scale);
		}
		t = null;
	}

	/**
	 * Computes the 3D inverse DCT (DCT-III) leaving the result in
	 * <code>a</code>. The data is stored in 1D array addressed in
	 * slice-major, then row-major, then column-major, in order of significance,
	 * i.e. the element (i,j,k) of 3D array x[n1][n2][n3] is stored in
	 * a[i*sliceStride + j*rowStride + k], where sliceStride = n2 * n3 and
	 * rowStride = n3.
	 * 
	 * @param a
	 *            data to transform
	 * @param scale
	 *            if true then scaling is performed
	 */
	public void inverse(float[] a, boolean scale) {
		int n, nw, nc;

		n = n1;
		if (n < n2) {
			n = n2;
		}
		if (n < n3) {
			n = n3;
		}
		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw);
		}
		nc = ip[1];
		if (n > nc) {
			nc = n;
			makect(nc, w, nw);
		}
		t = new float[nt];
		if ((np > 1) && (n1 * n2 * n3 >= DCT3D_THREADS_BEGIN_N)) {
			ddxt3da_subth(1, a, scale);
			ddxt3db_subth(1, a, scale);
		} else {
			ddxt3da_sub(1, a, scale);
			ddxt3db_sub(1, a, scale);
		}
		t = null;

	}

	/**
	 * Computes the 3D inverse DCT (DCT-III) leaving the result in
	 * <code>a</code>. The data is stored in 3D array.
	 * 
	 * @param a
	 *            data to transform
	 * @param scale
	 *            if true then scaling is performed
	 */
	public void inverse(float[][][] a, boolean scale) {
		int n, nw, nc;

		n = n1;
		if (n < n2) {
			n = n2;
		}
		if (n < n3) {
			n = n3;
		}
		nw = ip[0];
		if (n > (nw << 2)) {
			nw = n >> 2;
			makewt(nw);
		}
		nc = ip[1];
		if (n > nc) {
			nc = n;
			makect(nc, w, nw);
		}
		t = new float[nt];
		if ((np > 1) && (n1 * n2 * n3 >= DCT3D_THREADS_BEGIN_N)) {
			ddxt3da_subth(1, a, scale);
			ddxt3db_subth(1, a, scale);
		} else {
			ddxt3da_sub(1, a, scale);
			ddxt3db_sub(1, a, scale);
		}
		t = null;

	}

	/* -------- child routines -------- */

	private void ddxt3da_sub(int isgn, float[] a, boolean scale) {
		int i, j, k, idx0, idx1, idx2;

		if (isgn == -1) {
			for (i = 0; i < n1; i++) {
				idx0 = i * sliceStride;
				for (j = 0; j < n2; j++) {
					dctn3.forward(a, idx0 + j * rowStride, scale);
				}
				if (n3 > 2) {
					for (k = 0; k < n3; k += 4) {
						for (j = 0; j < n2; j++) {
							idx1 = idx0 + j * rowStride + k;
							idx2 = n2 + j;
							t[j] = a[idx1];
							t[idx2] = a[idx1 + 1];
							t[idx2 + n2] = a[idx1 + 2];
							t[idx2 + 2 * n2] = a[idx1 + 3];
						}
						dctn2.forward(t, 0, scale);
						dctn2.forward(t, n2, scale);
						dctn2.forward(t, 2 * n2, scale);
						dctn2.forward(t, 3 * n2, scale);
						for (j = 0; j < n2; j++) {
							idx1 = idx0 + j * rowStride + k;
							idx2 = n2 + j;
							a[idx1] = t[j];
							a[idx1 + 1] = t[idx2];
							a[idx1 + 2] = t[idx2 + n2];
							a[idx1 + 3] = t[idx2 + 2 * n2];
						}
					}
				} else if (n3 == 2) {
					for (j = 0; j < n2; j++) {
						idx1 = idx0 + j * rowStride;
						t[j] = a[idx1];
						t[n2 + j] = a[idx1 + 1];
					}
					dctn2.forward(t, 0, scale);
					dctn2.forward(t, n2, scale);
					for (j = 0; j < n2; j++) {
						idx1 = idx0 + j * rowStride;
						a[idx1] = t[j];
						a[idx1 + 1] = t[n2 + j];
					}
				}
			}
		} else {
			for (i = 0; i < n1; i++) {
				idx0 = i * sliceStride;
				for (j = 0; j < n2; j++) {
					dctn3.inverse(a, idx0 + j * rowStride, scale);
				}
				if (n3 > 2) {
					for (k = 0; k < n3; k += 4) {
						for (j = 0; j < n2; j++) {
							idx1 = idx0 + j * rowStride + k;
							idx2 = n2 + j;
							t[j] = a[idx1];
							t[idx2] = a[idx1 + 1];
							t[idx2 + n2] = a[idx1 + 2];
							t[idx2 + 2 * n2] = a[idx1 + 3];
						}
						dctn2.inverse(t, 0, scale);
						dctn2.inverse(t, n2, scale);
						dctn2.inverse(t, 2 * n2, scale);
						dctn2.inverse(t, 3 * n2, scale);
						for (j = 0; j < n2; j++) {
							idx1 = idx0 + j * rowStride + k;
							idx2 = n2 + j;
							a[idx1] = t[j];
							a[idx1 + 1] = t[idx2];
							a[idx1 + 2] = t[idx2 + n2];
							a[idx1 + 3] = t[idx2 + 2 * n2];
						}
					}
				} else if (n3 == 2) {
					for (j = 0; j < n2; j++) {
						idx1 = idx0 + j * rowStride;
						t[j] = a[idx1];
						t[n2 + j] = a[idx1 + 1];
					}
					dctn2.inverse(t, 0, scale);
					dctn2.inverse(t, n2, scale);
					for (j = 0; j < n2; j++) {
						idx1 = idx0 + j * rowStride;
						a[idx1] = t[j];
						a[idx1 + 1] = t[n2 + j];
					}
				}
			}
		}
	}

	private void ddxt3da_sub(int isgn, float[][][] a, boolean scale) {
		int i, j, k, idx2;

		if (isgn == -1) {
			for (i = 0; i < n1; i++) {
				for (j = 0; j < n2; j++) {
					dctn3.forward(a[i][j], scale);
				}
				if (n3 > 2) {
					for (k = 0; k < n3; k += 4) {
						for (j = 0; j < n2; j++) {
							idx2 = n2 + j;
							t[j] = a[i][j][k];
							t[idx2] = a[i][j][k + 1];
							t[idx2 + n2] = a[i][j][k + 2];
							t[idx2 + 2 * n2] = a[i][j][k + 3];
						}
						dctn2.forward(t, 0, scale);
						dctn2.forward(t, n2, scale);
						dctn2.forward(t, 2 * n2, scale);
						dctn2.forward(t, 3 * n2, scale);
						for (j = 0; j < n2; j++) {
							idx2 = n2 + j;
							a[i][j][k] = t[j];
							a[i][j][k + 1] = t[idx2];
							a[i][j][k + 2] = t[idx2 + n2];
							a[i][j][k + 3] = t[idx2 + 2 * n2];
						}
					}
				} else if (n3 == 2) {
					for (j = 0; j < n2; j++) {
						t[j] = a[i][j][0];
						t[n2 + j] = a[i][j][1];
					}
					dctn2.forward(t, 0, scale);
					dctn2.forward(t, n2, scale);
					for (j = 0; j < n2; j++) {
						a[i][j][0] = t[j];
						a[i][j][1] = t[n2 + j];
					}
				}
			}
		} else {
			for (i = 0; i < n1; i++) {
				for (j = 0; j < n2; j++) {
					dctn3.inverse(a[i][j], scale);
				}
				if (n3 > 2) {
					for (k = 0; k < n3; k += 4) {
						for (j = 0; j < n2; j++) {
							idx2 = n2 + j;
							t[j] = a[i][j][k];
							t[idx2] = a[i][j][k + 1];
							t[idx2 + n2] = a[i][j][k + 2];
							t[idx2 + 2 * n2] = a[i][j][k + 3];
						}
						dctn2.inverse(t, 0, scale);
						dctn2.inverse(t, n2, scale);
						dctn2.inverse(t, 2 * n2, scale);
						dctn2.inverse(t, 3 * n2, scale);
						for (j = 0; j < n2; j++) {
							idx2 = n2 + j;
							a[i][j][k] = t[j];
							a[i][j][k + 1] = t[idx2];
							a[i][j][k + 2] = t[idx2 + n2];
							a[i][j][k + 3] = t[idx2 + 2 * n2];
						}
					}
				} else if (n3 == 2) {
					for (j = 0; j < n2; j++) {
						t[j] = a[i][j][0];
						t[n2 + j] = a[i][j][1];
					}
					dctn2.inverse(t, 0, scale);
					dctn2.inverse(t, n2, scale);
					for (j = 0; j < n2; j++) {
						a[i][j][0] = t[j];
						a[i][j][1] = t[n2 + j];
					}
				}
			}
		}
	}

	private void ddxt3db_sub(int isgn, float[] a, boolean scale) {
		int i, j, k, idx0, idx1, idx2;

		if (isgn == -1) {
			if (n3 > 2) {
				for (j = 0; j < n2; j++) {
					idx0 = j * rowStride;
					for (k = 0; k < n3; k += 4) {
						for (i = 0; i < n1; i++) {
							idx1 = i * sliceStride + idx0 + k;
							idx2 = n1 + i;
							t[i] = a[idx1];
							t[idx2] = a[idx1 + 1];
							t[idx2 + n1] = a[idx1 + 2];
							t[idx2 + 2 * n1] = a[idx1 + 3];
						}
						dctn1.forward(t, 0, scale);
						dctn1.forward(t, n1, scale);
						dctn1.forward(t, 2 * n1, scale);
						dctn1.forward(t, 3 * n1, scale);
						for (i = 0; i < n1; i++) {
							idx1 = i * sliceStride + idx0 + k;
							idx2 = n1 + i;
							a[idx1] = t[i];
							a[idx1 + 1] = t[idx2];
							a[idx1 + 2] = t[idx2 + n1];
							a[idx1 + 3] = t[idx2 + 2 * n1];
						}
					}
				}
			} else if (n3 == 2) {
				for (j = 0; j < n2; j++) {
					idx0 = j * rowStride;
					for (i = 0; i < n1; i++) {
						idx1 = i * sliceStride + idx0;
						t[i] = a[idx1];
						t[n1 + i] = a[idx1 + 1];
					}
					dctn1.forward(t, 0, scale);
					dctn1.forward(t, n1, scale);
					for (i = 0; i < n1; i++) {
						idx1 = i * sliceStride + idx0;
						a[idx1] = t[i];
						a[idx1 + 1] = t[n1 + i];
					}
				}
			}
		} else {
			if (n3 > 2) {
				for (j = 0; j < n2; j++) {
					idx0 = j * rowStride;
					for (k = 0; k < n3; k += 4) {
						for (i = 0; i < n1; i++) {
							idx1 = i * sliceStride + idx0 + k;
							idx2 = n1 + i;
							t[i] = a[idx1];
							t[idx2] = a[idx1 + 1];
							t[idx2 + n1] = a[idx1 + 2];
							t[idx2 + 2 * n1] = a[idx1 + 3];
						}
						dctn1.inverse(t, 0, scale);
						dctn1.inverse(t, n1, scale);
						dctn1.inverse(t, 2 * n1, scale);
						dctn1.inverse(t, 3 * n1, scale);

						for (i = 0; i < n1; i++) {
							idx1 = i * sliceStride + idx0 + k;
							idx2 = n1 + i;
							a[idx1] = t[i];
							a[idx1 + 1] = t[idx2];
							a[idx1 + 2] = t[idx2 + n1];
							a[idx1 + 3] = t[idx2 + 2 * n1];
						}
					}
				}
			} else if (n3 == 2) {
				for (j = 0; j < n2; j++) {
					idx0 = j * rowStride;
					for (i = 0; i < n1; i++) {
						idx1 = i * sliceStride + idx0;
						t[i] = a[idx1];
						t[n1 + i] = a[idx1 + 1];
					}
					dctn1.inverse(t, 0, scale);
					dctn1.inverse(t, n1, scale);
					for (i = 0; i < n1; i++) {
						idx1 = i * sliceStride + idx0;
						a[idx1] = t[i];
						a[idx1 + 1] = t[n1 + i];
					}
				}
			}
		}
	}

	private void ddxt3db_sub(int isgn, float[][][] a, boolean scale) {
		int i, j, k, idx2;

		if (isgn == -1) {
			if (n3 > 2) {
				for (j = 0; j < n2; j++) {
					for (k = 0; k < n3; k += 4) {
						for (i = 0; i < n1; i++) {
							idx2 = n1 + i;
							t[i] = a[i][j][k];
							t[idx2] = a[i][j][k + 1];
							t[idx2 + n1] = a[i][j][k + 2];
							t[idx2 + 2 * n1] = a[i][j][k + 3];
						}
						dctn1.forward(t, 0, scale);
						dctn1.forward(t, n1, scale);
						dctn1.forward(t, 2 * n1, scale);
						dctn1.forward(t, 3 * n1, scale);
						for (i = 0; i < n1; i++) {
							idx2 = n1 + i;
							a[i][j][k] = t[i];
							a[i][j][k + 1] = t[idx2];
							a[i][j][k + 2] = t[idx2 + n1];
							a[i][j][k + 3] = t[idx2 + 2 * n1];
						}
					}
				}
			} else if (n3 == 2) {
				for (j = 0; j < n2; j++) {
					for (i = 0; i < n1; i++) {
						t[i] = a[i][j][0];
						t[n1 + i] = a[i][j][1];
					}
					dctn1.forward(t, 0, scale);
					dctn1.forward(t, n1, scale);
					for (i = 0; i < n1; i++) {
						a[i][j][0] = t[i];
						a[i][j][1] = t[n1 + i];
					}
				}
			}
		} else {
			if (n3 > 2) {
				for (j = 0; j < n2; j++) {
					for (k = 0; k < n3; k += 4) {
						for (i = 0; i < n1; i++) {
							idx2 = n1 + i;
							t[i] = a[i][j][k];
							t[idx2] = a[i][j][k + 1];
							t[idx2 + n1] = a[i][j][k + 2];
							t[idx2 + 2 * n1] = a[i][j][k + 3];
						}
						dctn1.inverse(t, 0, scale);
						dctn1.inverse(t, n1, scale);
						dctn1.inverse(t, 2 * n1, scale);
						dctn1.inverse(t, 3 * n1, scale);

						for (i = 0; i < n1; i++) {
							idx2 = n1 + i;
							a[i][j][k] = t[i];
							a[i][j][k + 1] = t[idx2];
							a[i][j][k + 2] = t[idx2 + n1];
							a[i][j][k + 3] = t[idx2 + 2 * n1];
						}
					}
				}
			} else if (n3 == 2) {
				for (j = 0; j < n2; j++) {
					for (i = 0; i < n1; i++) {
						t[i] = a[i][j][0];
						t[n1 + i] = a[i][j][1];
					}
					dctn1.inverse(t, 0, scale);
					dctn1.inverse(t, n1, scale);
					for (i = 0; i < n1; i++) {
						a[i][j][0] = t[i];
						a[i][j][1] = t[n1 + i];
					}
				}
			}
		}
	}

	private void ddxt3da_subth(final int isgn, final float[] a, final boolean scale) {
		final int nthread;
		int nt, i;

		if (np > n1) {
			nthread = n1;
		} else {
			nthread = np;
		}
		nt = 4 * n2;
		if (n3 == 2) {
			nt >>= 1;
		}
		Future[] futures = new Future[nthread];

		for (i = 0; i < nthread; i++) {
			final int n0 = i;
			final int startt = nt * i;
			futures[i] = Utils.threadPool.submit(new Runnable() {

				public void run() {
					int idx0, idx1, idx2;
					if (isgn == -1) {
						for (int i = n0; i < n1; i += nthread) {
							idx0 = i * sliceStride;
							for (int j = 0; j < n2; j++) {
								dctn3.forward(a, idx0 + j * rowStride, scale);
							}
							if (n3 > 2) {
								for (int k = 0; k < n3; k += 4) {
									for (int j = 0; j < n2; j++) {
										idx1 = idx0 + j * rowStride + k;
										idx2 = startt + n2 + j;
										t[startt + j] = a[idx1];
										t[idx2] = a[idx1 + 1];
										t[idx2 + n2] = a[idx1 + 2];
										t[idx2 + 2 * n2] = a[idx1 + 3];
									}
									dctn2.forward(t, startt, scale);
									dctn2.forward(t, startt + n2, scale);
									dctn2.forward(t, startt + 2 * n2, scale);
									dctn2.forward(t, startt + 3 * n2, scale);
									for (int j = 0; j < n2; j++) {
										idx1 = idx0 + j * rowStride + k;
										idx2 = startt + n2 + j;
										a[idx1] = t[startt + j];
										a[idx1 + 1] = t[idx2];
										a[idx1 + 2] = t[idx2 + n2];
										a[idx1 + 3] = t[idx2 + 2 * n2];
									}
								}
							} else if (n3 == 2) {
								for (int j = 0; j < n2; j++) {
									idx1 = idx0 + j * rowStride;
									t[startt + j] = a[idx1];
									t[startt + n2 + j] = a[idx1 + 1];
								}
								dctn2.forward(t, startt, scale);
								dctn2.forward(t, startt + n2, scale);
								for (int j = 0; j < n2; j++) {
									idx1 = idx0 + j * rowStride;
									a[idx1] = t[startt + j];
									a[idx1 + 1] = t[startt + n2 + j];
								}
							}
						}
					} else {
						for (int i = n0; i < n1; i += nthread) {
							idx0 = i * sliceStride;
							for (int j = 0; j < n2; j++) {
								dctn3.inverse(a, idx0 + j * rowStride, scale);
							}
							if (n3 > 2) {
								for (int k = 0; k < n3; k += 4) {
									for (int j = 0; j < n2; j++) {
										idx1 = idx0 + j * rowStride + k;
										idx2 = startt + n2 + j;
										t[startt + j] = a[idx1];
										t[idx2] = a[idx1 + 1];
										t[idx2 + n2] = a[idx1 + 2];
										t[idx2 + 2 * n2] = a[idx1 + 3];
									}
									dctn2.inverse(t, startt, scale);
									dctn2.inverse(t, startt + n2, scale);
									dctn2.inverse(t, startt + 2 * n2, scale);
									dctn2.inverse(t, startt + 3 * n2, scale);
									for (int j = 0; j < n2; j++) {
										idx1 = idx0 + j * rowStride + k;
										idx2 = startt + n2 + j;
										a[idx1] = t[startt + j];
										a[idx1 + 1] = t[idx2];
										a[idx1 + 2] = t[idx2 + n2];
										a[idx1 + 3] = t[idx2 + 2 * n2];
									}
								}
							} else if (n3 == 2) {
								for (int j = 0; j < n2; j++) {
									idx1 = idx0 + j * rowStride;
									t[startt + j] = a[idx1];
									t[startt + n2 + j] = a[idx1 + 1];
								}
								dctn2.inverse(t, startt, scale);
								dctn2.inverse(t, startt + n2, scale);
								for (int j = 0; j < n2; j++) {
									idx1 = idx0 + j * rowStride;
									a[idx1] = t[startt + j];
									a[idx1 + 1] = t[startt + n2 + j];
								}
							}
						}
					}
				}
			});
		}
		try {
			for (int j = 0; j < nthread; j++) {
				futures[j].get();
			}
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void ddxt3da_subth(final int isgn, final float[][][] a, final boolean scale) {
		final int nthread;
		int nt, i;

		if (np > n1) {
			nthread = n1;
		} else {
			nthread = np;
		}
		nt = 4 * n2;
		if (n3 == 2) {
			nt >>= 1;
		}
		Future[] futures = new Future[nthread];

		for (i = 0; i < nthread; i++) {
			final int n0 = i;
			final int startt = nt * i;
			futures[i] = Utils.threadPool.submit(new Runnable() {

				public void run() {
					int idx2;
					if (isgn == -1) {
						for (int i = n0; i < n1; i += nthread) {
							for (int j = 0; j < n2; j++) {
								dctn3.forward(a[i][j], scale);
							}
							if (n3 > 2) {
								for (int k = 0; k < n3; k += 4) {
									for (int j = 0; j < n2; j++) {
										idx2 = startt + n2 + j;
										t[startt + j] = a[i][j][k];
										t[idx2] = a[i][j][k + 1];
										t[idx2 + n2] = a[i][j][k + 2];
										t[idx2 + 2 * n2] = a[i][j][k + 3];
									}
									dctn2.forward(t, startt, scale);
									dctn2.forward(t, startt + n2, scale);
									dctn2.forward(t, startt + 2 * n2, scale);
									dctn2.forward(t, startt + 3 * n2, scale);
									for (int j = 0; j < n2; j++) {
										idx2 = startt + n2 + j;
										a[i][j][k] = t[startt + j];
										a[i][j][k + 1] = t[idx2];
										a[i][j][k + 2] = t[idx2 + n2];
										a[i][j][k + 3] = t[idx2 + 2 * n2];
									}
								}
							} else if (n3 == 2) {
								for (int j = 0; j < n2; j++) {
									t[startt + j] = a[i][j][0];
									t[startt + n2 + j] = a[i][j][1];
								}
								dctn2.forward(t, startt, scale);
								dctn2.forward(t, startt + n2, scale);
								for (int j = 0; j < n2; j++) {
									a[i][j][0] = t[startt + j];
									a[i][j][1] = t[startt + n2 + j];
								}
							}
						}
					} else {
						for (int i = n0; i < n1; i += nthread) {
							for (int j = 0; j < n2; j++) {
								dctn3.inverse(a[i][j], scale);
							}
							if (n3 > 2) {
								for (int k = 0; k < n3; k += 4) {
									for (int j = 0; j < n2; j++) {
										idx2 = startt + n2 + j;
										t[startt + j] = a[i][j][k];
										t[idx2] = a[i][j][k + 1];
										t[idx2 + n2] = a[i][j][k + 2];
										t[idx2 + 2 * n2] = a[i][j][k + 3];
									}
									dctn2.inverse(t, startt, scale);
									dctn2.inverse(t, startt + n2, scale);
									dctn2.inverse(t, startt + 2 * n2, scale);
									dctn2.inverse(t, startt + 3 * n2, scale);
									for (int j = 0; j < n2; j++) {
										idx2 = startt + n2 + j;
										a[i][j][k] = t[startt + j];
										a[i][j][k + 1] = t[idx2];
										a[i][j][k + 2] = t[idx2 + n2];
										a[i][j][k + 3] = t[idx2 + 2 * n2];
									}
								}
							} else if (n3 == 2) {
								for (int j = 0; j < n2; j++) {
									t[startt + j] = a[i][j][0];
									t[startt + n2 + j] = a[i][j][1];
								}
								dctn2.inverse(t, startt, scale);
								dctn2.inverse(t, startt + n2, scale);
								for (int j = 0; j < n2; j++) {
									a[i][j][0] = t[startt + j];
									a[i][j][1] = t[startt + n2 + j];
								}
							}
						}
					}
				}
			});
		}
		try {
			for (int j = 0; j < nthread; j++) {
				futures[j].get();
			}
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void ddxt3db_subth(final int isgn, final float[] a, final boolean scale) {
		final int nthread;
		int nt, i;

		if (np > n2) {
			nthread = n2;
		} else {
			nthread = np;
		}
		nt = 4 * n1;
		if (n3 == 2) {
			nt >>= 1;
		}
		Future[] futures = new Future[nthread];

		for (i = 0; i < nthread; i++) {
			final int n0 = i;
			final int startt = nt * i;
			futures[i] = Utils.threadPool.submit(new Runnable() {

				public void run() {
					int idx0, idx1, idx2;
					if (isgn == -1) {
						if (n3 > 2) {
							for (int j = n0; j < n2; j += nthread) {
								idx0 = j * rowStride;
								for (int k = 0; k < n3; k += 4) {
									for (int i = 0; i < n1; i++) {
										idx1 = i * sliceStride + idx0 + k;
										idx2 = startt + n1 + i;
										t[startt + i] = a[idx1];
										t[idx2] = a[idx1 + 1];
										t[idx2 + n1] = a[idx1 + 2];
										t[idx2 + 2 * n1] = a[idx1 + 3];
									}
									dctn1.forward(t, startt, scale);
									dctn1.forward(t, startt + n1, scale);
									dctn1.forward(t, startt + 2 * n1, scale);
									dctn1.forward(t, startt + 3 * n1, scale);
									for (int i = 0; i < n1; i++) {
										idx1 = i * sliceStride + idx0 + k;
										idx2 = startt + n1 + i;
										a[idx1] = t[startt + i];
										a[idx1 + 1] = t[idx2];
										a[idx1 + 2] = t[idx2 + n1];
										a[idx1 + 3] = t[idx2 + 2 * n1];
									}
								}
							}
						} else if (n3 == 2) {
							for (int j = n0; j < n2; j += nthread) {
								idx0 = j * rowStride;
								for (int i = 0; i < n1; i++) {
									idx1 = i * sliceStride + idx0;
									t[startt + i] = a[idx1];
									t[startt + n1 + i] = a[idx1 + 1];
								}
								dctn1.forward(t, startt, scale);
								dctn1.forward(t, startt + n1, scale);
								for (int i = 0; i < n1; i++) {
									idx1 = i * sliceStride + idx0;
									a[idx1] = t[startt + i];
									a[idx1 + 1] = t[startt + n1 + i];
								}
							}
						}
					} else {
						if (n3 > 2) {
							for (int j = n0; j < n2; j += nthread) {
								idx0 = j * rowStride;
								for (int k = 0; k < n3; k += 4) {
									for (int i = 0; i < n1; i++) {
										idx1 = i * sliceStride + idx0 + k;
										idx2 = startt + n1 + i;
										t[startt + i] = a[idx1];
										t[idx2] = a[idx1 + 1];
										t[idx2 + n1] = a[idx1 + 2];
										t[idx2 + 2 * n1] = a[idx1 + 3];
									}
									dctn1.inverse(t, startt, scale);
									dctn1.inverse(t, startt + n1, scale);
									dctn1.inverse(t, startt + 2 * n1, scale);
									dctn1.inverse(t, startt + 3 * n1, scale);
									for (int i = 0; i < n1; i++) {
										idx1 = i * sliceStride + idx0 + k;
										idx2 = startt + n1 + i;
										a[idx1] = t[startt + i];
										a[idx1 + 1] = t[idx2];
										a[idx1 + 2] = t[idx2 + n1];
										a[idx1 + 3] = t[idx2 + 2 * n1];
									}
								}
							}
						} else if (n3 == 2) {
							for (int j = n0; j < n2; j += nthread) {
								idx0 = j * rowStride;
								for (int i = 0; i < n1; i++) {
									idx1 = i * sliceStride + idx0;
									t[startt + i] = a[idx1];
									t[startt + n1 + i] = a[idx1 + 1];
								}
								dctn1.inverse(t, startt, scale);
								dctn1.inverse(t, startt + n1, scale);

								for (int i = 0; i < n1; i++) {
									idx1 = i * sliceStride + idx0;
									a[idx1] = t[startt + i];
									a[idx1 + 1] = t[startt + n1 + i];
								}
							}
						}
					}
				}
			});
		}
		try {
			for (int j = 0; j < nthread; j++) {
				futures[j].get();
			}
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void ddxt3db_subth(final int isgn, final float[][][] a, final boolean scale) {
		final int nthread;
		int nt, i;

		if (np > n2) {
			nthread = n2;
		} else {
			nthread = np;
		}
		nt = 4 * n1;
		if (n3 == 2) {
			nt >>= 1;
		}
		Future[] futures = new Future[nthread];

		for (i = 0; i < nthread; i++) {
			final int n0 = i;
			final int startt = nt * i;
			futures[i] = Utils.threadPool.submit(new Runnable() {

				public void run() {
					int idx2;
					if (isgn == -1) {
						if (n3 > 2) {
							for (int j = n0; j < n2; j += nthread) {
								for (int k = 0; k < n3; k += 4) {
									for (int i = 0; i < n1; i++) {
										idx2 = startt + n1 + i;
										t[startt + i] = a[i][j][k];
										t[idx2] = a[i][j][k + 1];
										t[idx2 + n1] = a[i][j][k + 2];
										t[idx2 + 2 * n1] = a[i][j][k + 3];
									}
									dctn1.forward(t, startt, scale);
									dctn1.forward(t, startt + n1, scale);
									dctn1.forward(t, startt + 2 * n1, scale);
									dctn1.forward(t, startt + 3 * n1, scale);
									for (int i = 0; i < n1; i++) {
										idx2 = startt + n1 + i;
										a[i][j][k] = t[startt + i];
										a[i][j][k + 1] = t[idx2];
										a[i][j][k + 2] = t[idx2 + n1];
										a[i][j][k + 3] = t[idx2 + 2 * n1];
									}
								}
							}
						} else if (n3 == 2) {
							for (int j = n0; j < n2; j += nthread) {
								for (int i = 0; i < n1; i++) {
									t[startt + i] = a[i][j][0];
									t[startt + n1 + i] = a[i][j][1];
								}
								dctn1.forward(t, startt, scale);
								dctn1.forward(t, startt + n1, scale);
								for (int i = 0; i < n1; i++) {
									a[i][j][0] = t[startt + i];
									a[i][j][1] = t[startt + n1 + i];
								}
							}
						}
					} else {
						if (n3 > 2) {
							for (int j = n0; j < n2; j += nthread) {
								for (int k = 0; k < n3; k += 4) {
									for (int i = 0; i < n1; i++) {
										idx2 = startt + n1 + i;
										t[startt + i] = a[i][j][k];
										t[idx2] = a[i][j][k + 1];
										t[idx2 + n1] = a[i][j][k + 2];
										t[idx2 + 2 * n1] = a[i][j][k + 3];
									}
									dctn1.inverse(t, startt, scale);
									dctn1.inverse(t, startt + n1, scale);
									dctn1.inverse(t, startt + 2 * n1, scale);
									dctn1.inverse(t, startt + 3 * n1, scale);
									for (int i = 0; i < n1; i++) {
										idx2 = startt + n1 + i;
										a[i][j][k] = t[startt + i];
										a[i][j][k + 1] = t[idx2];
										a[i][j][k + 2] = t[idx2 + n1];
										a[i][j][k + 3] = t[idx2 + 2 * n1];
									}
								}
							}
						} else if (n3 == 2) {
							for (int j = n0; j < n2; j += nthread) {
								for (int i = 0; i < n1; i++) {
									t[startt + i] = a[i][j][0];
									t[startt + n1 + i] = a[i][j][1];
								}
								dctn1.inverse(t, startt, scale);
								dctn1.inverse(t, startt + n1, scale);

								for (int i = 0; i < n1; i++) {
									a[i][j][0] = t[startt + i];
									a[i][j][1] = t[startt + n1 + i];
								}
							}
						}
					}
				}
			});
		}
		try {
			for (int j = 0; j < nthread; j++) {
				futures[j].get();
			}
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/* -------- initializing routines -------- */

	private void makewt(int nw) {
		int j, nwh, nw0, nw1;
		float delta, wn4r, wk1r, wk1i, wk3r, wk3i;

		ip[0] = nw;
		ip[1] = 1;
		if (nw > 2) {
			nwh = nw >> 1;
			delta = (float) (Math.atan(1.0) / nwh);
			wn4r = (float) Math.cos(delta * nwh);
			w[0] = 1;
			w[1] = wn4r;
			if (nwh == 4) {
				w[2] = (float) Math.cos(delta * 2);
				w[3] = (float) Math.sin(delta * 2);
			} else if (nwh > 4) {
				makeipt(nw);
				w[2] = (float) (0.5 / Math.cos(delta * 2));
				w[3] = (float) (0.5 / Math.cos(delta * 6));
				for (j = 4; j < nwh; j += 4) {
					w[j] = (float) Math.cos(delta * j);
					w[j + 1] = (float) Math.sin(delta * j);
					w[j + 2] = (float) Math.cos(3 * delta * j);
					w[j + 3] = (float) -Math.sin(3 * delta * j);
				}
			}
			nw0 = 0;
			while (nwh > 2) {
				nw1 = nw0 + nwh;
				nwh >>= 1;
				w[nw1] = 1;
				w[nw1 + 1] = wn4r;
				if (nwh == 4) {
					wk1r = w[nw0 + 4];
					wk1i = w[nw0 + 5];
					w[nw1 + 2] = wk1r;
					w[nw1 + 3] = wk1i;
				} else if (nwh > 4) {
					wk1r = w[nw0 + 4];
					wk3r = w[nw0 + 6];
					w[nw1 + 2] = (float) (0.5 / wk1r);
					w[nw1 + 3] = (float) (0.5 / wk3r);
					for (j = 4; j < nwh; j += 4) {
						int idx1 = nw0 + 2 * j;
						int idx2 = nw1 + j;
						wk1r = w[idx1];
						wk1i = w[idx1 + 1];
						wk3r = w[idx1 + 2];
						wk3i = w[idx1 + 3];
						w[idx2] = wk1r;
						w[idx2 + 1] = wk1i;
						w[idx2 + 2] = wk3r;
						w[idx2 + 3] = wk3i;
					}
				}
				nw0 = nw1;
			}
		}
	}

	private void makeipt(int nw) {
		int j, l, m, m2, p, q;

		ip[2] = 0;
		ip[3] = 16;
		m = 2;
		for (l = nw; l > 32; l >>= 2) {
			m2 = m << 1;
			q = m2 << 3;
			for (j = m; j < m2; j++) {
				p = ip[j] << 2;
				ip[m + j] = p;
				ip[m2 + j] = p + q;
			}
			m = m2;
		}
	}

	private void makect(int nc, float[] c, int startc) {
		int j, nch;
		float delta;

		ip[1] = nc;
		if (nc > 1) {
			nch = nc >> 1;
			delta = (float) Math.atan(1.0) / nch;
			c[startc] = (float) Math.cos(delta * nch);
			c[startc + nch] = 0.5f * c[startc];
			for (j = 1; j < nch; j++) {
				c[startc + j] = (float) (0.5 * Math.cos(delta * j));
				c[startc + nc - j] = (float) (0.5 * Math.sin(delta * j));
			}
		}
	}
}
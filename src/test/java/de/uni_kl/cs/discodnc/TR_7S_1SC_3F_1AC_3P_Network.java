/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.NetworkFactory;
import de.uni_kl.cs.discodnc.network.Server;

public class TR_7S_1SC_3F_1AC_3P_Network implements NetworkFactory {
	private static final int sc_R = 20;
	private static final int sc_T = 20;
	private static final int ac_r = 5;
	private static final int ac_b = 25;
	
	private Server s0, s1, s2, s3, s4, s5, s6;
	
	private ServiceCurve service_curve = CurvePwAffine.getFactory().createRateLatency(sc_R, sc_T);
	private ArrivalCurve arrival_curve = CurvePwAffine.getFactory().createTokenBucket(ac_r, ac_b);
	
	private Network network;

	public TR_7S_1SC_3F_1AC_3P_Network() {
		network = createNetwork();
	}

	public Network getNetwork() {
		return network;
	}

	public Network createNetwork() {
		network = new Network();

		s0 = network.addServer(service_curve);
		s1 = network.addServer(service_curve);
		s2 = network.addServer(service_curve);
		s3 = network.addServer(service_curve);
		s4 = network.addServer(service_curve);
		s5 = network.addServer(service_curve);
		s6 = network.addServer(service_curve);

		try {
			network.addLink(s0, s1);
			network.addLink(s1, s2);
			network.addLink(s2, s5);
			network.addLink(s3, s4);
			network.addLink(s4, s5);
			network.addLink(s5, s6);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try {
			network.addFlow("f0", arrival_curve, s1, s6);
			network.addFlow("f1", arrival_curve, s0, s6);
			network.addFlow("f2", arrival_curve, s3, s6);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return network;
	}

	public void reinitializeCurves() {
		service_curve = CurvePwAffine.getFactory().createRateLatency(sc_R, sc_T);
		for (Server server : network.getServers()) {
			server.setServiceCurve(service_curve);
		}

		arrival_curve = CurvePwAffine.getFactory().createTokenBucket(ac_r, ac_b);
		for (Flow flow : network.getFlows()) {
			flow.setArrivalCurve(arrival_curve);
		}
	}
}
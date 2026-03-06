CREATE TABLE public.locomotive (
    id integer NOT NULL,
    model character varying(100) NOT NULL,
    power integer NOT NULL,
    status character varying(50) NOT NULL,
    CONSTRAINT locomotive_power_check CHECK ((power > 0)),
    CONSTRAINT locomotive_status_check CHECK (((status)::text = ANY (ARRAY[('active'::character varying)::text, ('inactive'::character varying)::text])))
);


ALTER TABLE public.locomotive OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16398)
-- Name: locomotive_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.locomotive_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.locomotive_id_seq OWNER TO postgres;

--
-- TOC entry 4978 (class 0 OID 0)
-- Dependencies: 220
-- Name: locomotive_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.locomotive_id_seq OWNED BY public.locomotive.id;


--
-- TOC entry 221 (class 1259 OID 16399)
-- Name: schedule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule (
    id integer NOT NULL,
    train_id integer NOT NULL,
    departure_time timestamp without time zone NOT NULL,
    arrival_time timestamp without time zone NOT NULL,
    CONSTRAINT schedule_check CHECK ((arrival_time > departure_time))
);


ALTER TABLE public.schedule OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16407)
-- Name: schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.schedule_id_seq OWNER TO postgres;

--
-- TOC entry 4979 (class 0 OID 0)
-- Dependencies: 222
-- Name: schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_id_seq OWNED BY public.schedule.id;


--
-- TOC entry 223 (class 1259 OID 16408)
-- Name: station; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.station (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.station OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16413)
-- Name: station_entry; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.station_entry (
    id integer NOT NULL,
    schedule_id integer NOT NULL,
    station_id integer NOT NULL,
    departure_time timestamp without time zone NOT NULL,
    arrival_time timestamp without time zone NOT NULL,
    segment_order integer NOT NULL,
    CONSTRAINT station_entry_check CHECK ((departure_time >= arrival_time)),
    CONSTRAINT station_entry_segment_order_check CHECK ((segment_order > 0))
);


ALTER TABLE public.station_entry OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16424)
-- Name: station_entry_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.station_entry_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.station_entry_id_seq OWNER TO postgres;

--
-- TOC entry 4980 (class 0 OID 0)
-- Dependencies: 225
-- Name: station_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.station_entry_id_seq OWNED BY public.station_entry.id;


--
-- TOC entry 226 (class 1259 OID 16425)
-- Name: station_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.station_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.station_id_seq OWNER TO postgres;

--
-- TOC entry 4981 (class 0 OID 0)
-- Dependencies: 226
-- Name: station_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.station_id_seq OWNED BY public.station.id;


--
-- TOC entry 227 (class 1259 OID 16426)
-- Name: train; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.train (
    id integer NOT NULL,
    number character varying(20) NOT NULL,
    locomotive_id integer NOT NULL,
    max_speed integer NOT NULL,
    CONSTRAINT train_max_speed_check CHECK ((max_speed > 0))
);


ALTER TABLE public.train OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16434)
-- Name: train_carriage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.train_carriage (
    id integer NOT NULL,
    train_id integer NOT NULL,
    type character varying(50) NOT NULL,
    CONSTRAINT train_carriage_type_check CHECK (((type)::text = ANY (ARRAY[('passenger'::character varying)::text, ('freight'::character varying)::text, ('restaurant'::character varying)::text, ('sleeping'::character varying)::text])))
);


ALTER TABLE public.train_carriage OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16441)
-- Name: train_carriage_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.train_carriage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.train_carriage_id_seq OWNER TO postgres;

--
-- TOC entry 4982 (class 0 OID 0)
-- Dependencies: 229
-- Name: train_carriage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.train_carriage_id_seq OWNED BY public.train_carriage.id;


--
-- TOC entry 230 (class 1259 OID 16442)
-- Name: train_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.train_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.train_id_seq OWNER TO postgres;

--
-- TOC entry 4983 (class 0 OID 0)
-- Dependencies: 230
-- Name: train_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.train_id_seq OWNED BY public.train.id;


--
-- TOC entry 4780 (class 2604 OID 16443)
-- Name: locomotive id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locomotive ALTER COLUMN id SET DEFAULT nextval('public.locomotive_id_seq'::regclass);


--
-- TOC entry 4781 (class 2604 OID 16444)
-- Name: schedule id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule ALTER COLUMN id SET DEFAULT nextval('public.schedule_id_seq'::regclass);


--
-- TOC entry 4782 (class 2604 OID 16445)
-- Name: station id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station ALTER COLUMN id SET DEFAULT nextval('public.station_id_seq'::regclass);


--
-- TOC entry 4783 (class 2604 OID 16446)
-- Name: station_entry id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station_entry ALTER COLUMN id SET DEFAULT nextval('public.station_entry_id_seq'::regclass);


--
-- TOC entry 4784 (class 2604 OID 16447)
-- Name: train id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train ALTER COLUMN id SET DEFAULT nextval('public.train_id_seq'::regclass);


--
-- TOC entry 4785 (class 2604 OID 16448)
-- Name: train_carriage id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train_carriage ALTER COLUMN id SET DEFAULT nextval('public.train_carriage_id_seq'::regclass);


--
-- TOC entry 4961 (class 0 OID 16389)
-- Dependencies: 219
-- Data for Name: locomotive; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.locomotive (id, model, power, status) FROM stdin;
197	VL80	8000	active
2	Model_2	1100	active
198	VL85	8500	active
4	Model_4	1200	active
5	Model_5	1250	inactive
7	Model_7	1350	active
8	Model_8	1400	active
199	EP20	7200	active
10	Model_10	1500	inactive
200	2ES6	8800	active
12	Model_12	1600	active
13	Model_13	1650	active
14	Model_14	1700	active
15	Model_15	1750	inactive
16	Model_16	1800	active
17	Model_17	1850	active
18	Model_18	1900	active
19	Model_19	1950	active
20	Model_20	2000	inactive
21	Model_21	2050	active
22	Model_22	2100	active
23	Model_23	2150	active
24	Model_24	2200	active
25	Model_25	2250	inactive
26	Model_26	2300	active
27	Model_27	2350	active
28	Model_28	2400	active
29	Model_29	2450	active
30	Model_30	2500	inactive
31	Model_31	2550	active
32	Model_32	2600	active
33	Model_33	2650	active
34	Model_34	2700	active
35	Model_35	2750	inactive
36	Model_36	2800	active
37	Model_37	2850	active
38	Model_38	2900	active
39	Model_39	2950	active
40	Model_40	3000	inactive
41	Model_41	3050	active
42	Model_42	3100	active
43	Model_43	3150	active
44	Model_44	3200	active
45	Model_45	3250	inactive
46	Model_46	3300	active
47	Model_47	3350	active
48	Model_48	3400	active
49	Model_49	3450	active
201	TEP70	4000	active
50	ЧС7	110	active
202	2TE116	6000	inactive
203	ChS4T	4000	active
204	TEM18	1800	active
205	ChME3	1350	active
206	TGM6	1200	active
55	Model_11111	123	active
157	VL80	8000	active
158	VL85	8500	active
159	EP20	7200	active
160	2ES6	8800	active
161	TEP70	4000	active
162	2TE116	6000	inactive
163	ChS4T	4000	active
164	TEM18	1800	active
165	ChME3	1350	active
166	TGM6	1200	active
177	VL80	8000	active
178	VL85	8500	active
179	EP20	7200	active
180	2ES6	8800	active
181	TEP70	4000	active
182	2TE116	6000	inactive
183	ChS4T	4000	active
184	TEM18	1800	active
185	ChME3	1350	active
186	TGM6	1200	active
6	Model_6	1301	active
\.


--
-- TOC entry 4963 (class 0 OID 16399)
-- Dependencies: 221
-- Data for Name: schedule; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.schedule (id, train_id, departure_time, arrival_time) FROM stdin;
302	51	2025-12-14 17:02:00	2025-12-14 18:02:00
4	4	2025-09-27 07:11:27.026592	2025-09-27 09:11:27.026592
389	195	2025-12-15 05:00:00	2025-12-15 17:00:00
390	195	2025-12-17 11:00:00	2025-12-17 23:00:00
391	196	2025-12-16 05:00:00	2025-12-16 17:00:00
392	197	2025-12-17 05:00:00	2025-12-17 17:00:00
393	197	2025-12-19 11:00:00	2025-12-19 23:00:00
394	198	2025-12-18 05:00:00	2025-12-18 17:00:00
395	199	2025-12-19 05:00:00	2025-12-19 17:00:00
396	199	2025-12-21 11:00:00	2025-12-21 23:00:00
397	200	2025-12-20 05:00:00	2025-12-20 17:00:00
398	200	2025-12-22 11:00:00	2025-12-22 23:00:00
399	201	2025-12-21 05:00:00	2025-12-21 17:00:00
400	202	2025-12-22 05:00:00	2025-12-22 17:00:00
401	203	2025-12-23 05:00:00	2025-12-23 17:00:00
402	204	2025-12-24 05:00:00	2025-12-24 17:00:00
417	217	2025-12-15 05:00:00	2025-12-15 17:00:00
418	217	2025-12-17 11:00:00	2025-12-17 23:00:00
419	218	2025-12-16 05:00:00	2025-12-16 17:00:00
420	219	2025-12-17 05:00:00	2025-12-17 17:00:00
421	219	2025-12-19 11:00:00	2025-12-19 23:00:00
422	220	2025-12-18 05:00:00	2025-12-18 17:00:00
423	221	2025-12-19 05:00:00	2025-12-19 17:00:00
424	221	2025-12-21 11:00:00	2025-12-21 23:00:00
425	222	2025-12-20 05:00:00	2025-12-20 17:00:00
426	222	2025-12-22 11:00:00	2025-12-22 23:00:00
427	223	2025-12-21 05:00:00	2025-12-21 17:00:00
428	224	2025-12-22 05:00:00	2025-12-22 17:00:00
429	225	2025-12-23 05:00:00	2025-12-23 17:00:00
430	226	2025-12-24 05:00:00	2025-12-24 17:00:00
51	51	2025-09-29 06:11:27.026592	2025-09-29 08:11:27.026592
104	4	2025-10-01 11:11:27.026592	2025-10-01 13:11:27.026592
151	51	2025-10-03 10:11:27.026592	2025-10-03 12:11:27.026592
403	206	2025-12-15 05:00:00	2025-12-15 17:00:00
404	206	2025-12-17 11:00:00	2025-12-17 23:00:00
405	207	2025-12-16 05:00:00	2025-12-16 17:00:00
406	208	2025-12-17 05:00:00	2025-12-17 17:00:00
407	208	2025-12-19 11:00:00	2025-12-19 23:00:00
408	209	2025-12-18 05:00:00	2025-12-18 17:00:00
409	210	2025-12-19 05:00:00	2025-12-19 17:00:00
410	210	2025-12-21 11:00:00	2025-12-21 23:00:00
411	211	2025-12-20 05:00:00	2025-12-20 17:00:00
412	211	2025-12-22 11:00:00	2025-12-22 23:00:00
413	212	2025-12-21 05:00:00	2025-12-21 17:00:00
414	213	2025-12-22 05:00:00	2025-12-22 17:00:00
415	214	2025-12-23 05:00:00	2025-12-23 17:00:00
416	215	2025-12-24 05:00:00	2025-12-24 17:00:00
431	103	2025-12-14 21:26:00	2025-12-14 22:26:00
204	4	2025-10-05 15:11:27.026592	2025-10-05 17:11:27.026592
251	51	2025-10-07 14:11:27.026592	2025-10-07 16:11:27.026592
\.


--
-- TOC entry 4965 (class 0 OID 16408)
-- Dependencies: 223
-- Data for Name: station; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.station (id, name) FROM stdin;
143	Moscow Central
3	Station_3
4	Station_4
5	Station_5
6	Station_6
7	Station_7
8	Station_8
9	Station_9
11	Station_11
12	Station_12
13	Station_13
14	Station_14
15	Station_15
16	Station_16
17	Station_17
19	Station_19
144	St. Petersburg
20	Station_240
145	Nizhny Novgorod
146	Kazan
147	Samara
2	Station_1
10	Station_1102k
148	Yekaterinburg
149	Novosibirsk
150	Krasnoyarsk
151	Irkutsk
152	Vladivostok
163	Moscow Central
164	St. Petersburg
165	Nizhny Novgorod
166	Kazan
167	Samara
168	Yekaterinburg
169	Novosibirsk
170	Krasnoyarsk
171	Irkutsk
172	Vladivostok
183	Moscow Central
184	St. Petersburg
185	Nizhny Novgorod
186	Kazan
187	Samara
188	Yekaterinburg
189	Novosibirsk
190	Krasnoyarsk
191	Irkutsk
192	Vladivostok
\.


--
-- TOC entry 4966 (class 0 OID 16413)
-- Dependencies: 224
-- Data for Name: station_entry; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.station_entry (id, schedule_id, station_id, departure_time, arrival_time, segment_order) FROM stdin;
1208	389	145	2025-12-15 05:05:00	2025-12-15 05:00:00	1
1210	389	147	2025-12-15 09:18:00	2025-12-15 09:11:00	3
1211	389	148	2025-12-15 11:26:00	2025-12-15 11:18:00	4
1212	389	149	2025-12-15 13:35:00	2025-12-15 13:26:00	5
1213	389	150	2025-12-15 15:45:00	2025-12-15 15:35:00	6
1214	389	151	2025-12-15 17:45:00	2025-12-15 17:45:00	7
1215	390	148	2025-12-17 11:05:00	2025-12-17 11:00:00	1
1216	390	149	2025-12-17 17:11:00	2025-12-17 17:05:00	2
1217	390	150	2025-12-17 23:11:00	2025-12-17 23:11:00	3
1218	391	144	2025-12-16 05:05:00	2025-12-16 05:00:00	1
1219	391	145	2025-12-16 09:11:00	2025-12-16 09:05:00	2
1220	391	146	2025-12-16 13:18:00	2025-12-16 13:11:00	3
1221	391	147	2025-12-16 17:18:00	2025-12-16 17:18:00	4
1222	392	145	2025-12-17 05:05:00	2025-12-17 05:00:00	1
1223	392	146	2025-12-17 08:11:00	2025-12-17 08:05:00	2
1224	392	147	2025-12-17 11:18:00	2025-12-17 11:11:00	3
1225	392	148	2025-12-17 14:26:00	2025-12-17 14:18:00	4
1226	392	149	2025-12-17 17:26:00	2025-12-17 17:26:00	5
1227	393	144	2025-12-19 11:05:00	2025-12-19 11:00:00	1
1228	393	145	2025-12-19 13:35:00	2025-12-19 13:29:00	2
1229	393	146	2025-12-19 16:06:00	2025-12-19 15:59:00	3
1231	393	148	2025-12-19 21:11:00	2025-12-19 21:02:00	5
1232	393	149	2025-12-19 23:35:00	2025-12-19 23:35:00	6
1233	394	144	2025-12-18 05:05:00	2025-12-18 05:00:00	1
1234	394	145	2025-12-18 07:11:00	2025-12-18 07:05:00	2
1235	394	146	2025-12-18 09:18:00	2025-12-18 09:11:00	3
1236	394	147	2025-12-18 11:26:00	2025-12-18 11:18:00	4
1237	394	148	2025-12-18 13:35:00	2025-12-18 13:26:00	5
1238	394	149	2025-12-18 15:45:00	2025-12-18 15:35:00	6
1239	394	150	2025-12-18 17:45:00	2025-12-18 17:45:00	7
1240	395	146	2025-12-19 05:05:00	2025-12-19 05:00:00	1
1241	395	147	2025-12-19 11:11:00	2025-12-19 11:05:00	2
1242	395	148	2025-12-19 17:11:00	2025-12-19 17:11:00	3
1243	396	143	2025-12-21 11:05:00	2025-12-21 11:00:00	1
1244	396	144	2025-12-21 15:11:00	2025-12-21 15:05:00	2
1245	396	145	2025-12-21 19:18:00	2025-12-21 19:11:00	3
1246	396	146	2025-12-21 23:18:00	2025-12-21 23:18:00	4
1247	397	145	2025-12-20 05:05:00	2025-12-20 05:00:00	1
1248	397	146	2025-12-20 08:11:00	2025-12-20 08:05:00	2
1249	397	147	2025-12-20 11:18:00	2025-12-20 11:11:00	3
1250	397	148	2025-12-20 14:26:00	2025-12-20 14:18:00	4
1251	397	149	2025-12-20 17:26:00	2025-12-20 17:26:00	5
1252	398	145	2025-12-22 11:05:00	2025-12-22 11:00:00	1
1253	398	146	2025-12-22 13:35:00	2025-12-22 13:29:00	2
1254	398	147	2025-12-22 16:06:00	2025-12-22 15:59:00	3
1255	398	148	2025-12-22 18:38:00	2025-12-22 18:30:00	4
1256	398	149	2025-12-22 21:11:00	2025-12-22 21:02:00	5
1257	398	150	2025-12-22 23:35:00	2025-12-22 23:35:00	6
1258	399	143	2025-12-21 05:05:00	2025-12-21 05:00:00	1
1259	399	144	2025-12-21 07:11:00	2025-12-21 07:05:00	2
1260	399	145	2025-12-21 09:18:00	2025-12-21 09:11:00	3
1261	399	146	2025-12-21 11:26:00	2025-12-21 11:18:00	4
1262	399	147	2025-12-21 13:35:00	2025-12-21 13:26:00	5
1263	399	148	2025-12-21 15:45:00	2025-12-21 15:35:00	6
1264	399	149	2025-12-21 17:45:00	2025-12-21 17:45:00	7
1265	400	144	2025-12-22 05:05:00	2025-12-22 05:00:00	1
1266	400	145	2025-12-22 11:11:00	2025-12-22 11:05:00	2
1267	400	146	2025-12-22 17:11:00	2025-12-22 17:11:00	3
1268	401	148	2025-12-23 05:05:00	2025-12-23 05:00:00	1
1269	401	149	2025-12-23 09:11:00	2025-12-23 09:05:00	2
1270	401	150	2025-12-23 13:18:00	2025-12-23 13:11:00	3
1271	401	151	2025-12-23 17:18:00	2025-12-23 17:18:00	4
1272	402	145	2025-12-24 05:05:00	2025-12-24 05:00:00	1
1273	402	146	2025-12-24 08:11:00	2025-12-24 08:05:00	2
1274	402	147	2025-12-24 11:18:00	2025-12-24 11:11:00	3
1275	402	148	2025-12-24 14:26:00	2025-12-24 14:18:00	4
1276	402	149	2025-12-24 17:26:00	2025-12-24 17:26:00	5
1230	393	147	2025-12-19 18:39:00	2025-12-19 18:30:00	4
1209	389	146	2025-12-15 07:12:00	2025-12-15 07:05:00	2
1277	403	166	2025-12-15 05:05:00	2025-12-15 05:00:00	1
1278	403	167	2025-12-15 07:35:00	2025-12-15 07:29:00	2
1279	403	168	2025-12-15 10:06:00	2025-12-15 09:59:00	3
1280	403	169	2025-12-15 12:38:00	2025-12-15 12:30:00	4
1281	403	170	2025-12-15 15:11:00	2025-12-15 15:02:00	5
1282	403	171	2025-12-15 17:35:00	2025-12-15 17:35:00	6
1283	404	165	2025-12-17 11:05:00	2025-12-17 11:00:00	1
1284	404	166	2025-12-17 13:11:00	2025-12-17 13:05:00	2
1285	404	167	2025-12-17 15:18:00	2025-12-17 15:11:00	3
1286	404	168	2025-12-17 17:26:00	2025-12-17 17:18:00	4
1287	404	169	2025-12-17 19:35:00	2025-12-17 19:26:00	5
1288	404	170	2025-12-17 21:45:00	2025-12-17 21:35:00	6
1289	404	171	2025-12-17 23:45:00	2025-12-17 23:45:00	7
1290	405	169	2025-12-16 05:05:00	2025-12-16 05:00:00	1
1291	405	170	2025-12-16 11:11:00	2025-12-16 11:05:00	2
1292	405	171	2025-12-16 17:11:00	2025-12-16 17:11:00	3
1293	406	167	2025-12-17 05:05:00	2025-12-17 05:00:00	1
1294	406	168	2025-12-17 09:11:00	2025-12-17 09:05:00	2
1295	406	169	2025-12-17 13:18:00	2025-12-17 13:11:00	3
1296	406	170	2025-12-17 17:18:00	2025-12-17 17:18:00	4
1297	407	165	2025-12-19 11:05:00	2025-12-19 11:00:00	1
1298	407	166	2025-12-19 14:11:00	2025-12-19 14:05:00	2
1299	407	167	2025-12-19 17:18:00	2025-12-19 17:11:00	3
1300	407	168	2025-12-19 20:26:00	2025-12-19 20:18:00	4
1301	407	169	2025-12-19 23:26:00	2025-12-19 23:26:00	5
1302	408	163	2025-12-18 05:05:00	2025-12-18 05:00:00	1
1303	408	164	2025-12-18 07:35:00	2025-12-18 07:29:00	2
1304	408	165	2025-12-18 10:06:00	2025-12-18 09:59:00	3
1305	408	166	2025-12-18 12:38:00	2025-12-18 12:30:00	4
1306	408	167	2025-12-18 15:11:00	2025-12-18 15:02:00	5
1307	408	168	2025-12-18 17:35:00	2025-12-18 17:35:00	6
1308	409	164	2025-12-19 05:05:00	2025-12-19 05:00:00	1
1309	409	165	2025-12-19 07:11:00	2025-12-19 07:05:00	2
1310	409	166	2025-12-19 09:18:00	2025-12-19 09:11:00	3
1311	409	167	2025-12-19 11:26:00	2025-12-19 11:18:00	4
1312	409	168	2025-12-19 13:35:00	2025-12-19 13:26:00	5
1313	409	169	2025-12-19 15:45:00	2025-12-19 15:35:00	6
1314	409	170	2025-12-19 17:45:00	2025-12-19 17:45:00	7
1318	411	166	2025-12-20 05:05:00	2025-12-20 05:00:00	1
1319	411	167	2025-12-20 09:11:00	2025-12-20 09:05:00	2
1320	411	168	2025-12-20 13:18:00	2025-12-20 13:11:00	3
1321	411	169	2025-12-20 17:18:00	2025-12-20 17:18:00	4
1322	412	165	2025-12-22 11:05:00	2025-12-22 11:00:00	1
1323	412	166	2025-12-22 14:11:00	2025-12-22 14:05:00	2
1324	412	167	2025-12-22 17:18:00	2025-12-22 17:11:00	3
1325	412	168	2025-12-22 20:26:00	2025-12-22 20:18:00	4
1326	412	169	2025-12-22 23:26:00	2025-12-22 23:26:00	5
1327	413	164	2025-12-21 05:05:00	2025-12-21 05:00:00	1
1328	413	165	2025-12-21 07:35:00	2025-12-21 07:29:00	2
1329	413	166	2025-12-21 10:06:00	2025-12-21 09:59:00	3
1330	413	167	2025-12-21 12:38:00	2025-12-21 12:30:00	4
1331	413	168	2025-12-21 15:11:00	2025-12-21 15:02:00	5
1332	413	169	2025-12-21 17:35:00	2025-12-21 17:35:00	6
1333	414	163	2025-12-22 05:05:00	2025-12-22 05:00:00	1
1334	414	164	2025-12-22 07:11:00	2025-12-22 07:05:00	2
1335	414	165	2025-12-22 09:18:00	2025-12-22 09:11:00	3
1336	414	166	2025-12-22 11:26:00	2025-12-22 11:18:00	4
1337	414	167	2025-12-22 13:35:00	2025-12-22 13:26:00	5
1338	414	168	2025-12-22 15:45:00	2025-12-22 15:35:00	6
1339	414	169	2025-12-22 17:45:00	2025-12-22 17:45:00	7
1340	415	165	2025-12-23 05:05:00	2025-12-23 05:00:00	1
1341	415	166	2025-12-23 11:11:00	2025-12-23 11:05:00	2
1342	415	167	2025-12-23 17:11:00	2025-12-23 17:11:00	3
1343	416	165	2025-12-24 05:05:00	2025-12-24 05:00:00	1
1344	416	166	2025-12-24 09:11:00	2025-12-24 09:05:00	2
1345	416	167	2025-12-24 13:18:00	2025-12-24 13:11:00	3
1346	416	168	2025-12-24 17:18:00	2025-12-24 17:18:00	4
1315	410	167	2025-12-21 11:05:00	2025-12-21 11:00:00	2
1316	410	168	2025-12-21 17:11:00	2025-12-21 17:05:00	3
1317	410	169	2025-12-21 23:11:00	2025-12-21 23:11:00	4
1421	4	11	2005-03-25 22:22:00	2005-03-24 21:22:00	1
1347	417	185	2025-12-15 05:05:00	2025-12-15 05:00:00	1
1348	417	186	2025-12-15 08:11:00	2025-12-15 08:05:00	2
1349	417	187	2025-12-15 11:18:00	2025-12-15 11:11:00	3
1350	417	188	2025-12-15 14:26:00	2025-12-15 14:18:00	4
1351	417	189	2025-12-15 17:26:00	2025-12-15 17:26:00	5
1352	418	185	2025-12-17 11:05:00	2025-12-17 11:00:00	1
1353	418	186	2025-12-17 13:35:00	2025-12-17 13:29:00	2
1354	418	187	2025-12-17 16:06:00	2025-12-17 15:59:00	3
1355	418	188	2025-12-17 18:38:00	2025-12-17 18:30:00	4
1356	418	189	2025-12-17 21:11:00	2025-12-17 21:02:00	5
1357	418	190	2025-12-17 23:35:00	2025-12-17 23:35:00	6
1358	419	185	2025-12-16 05:05:00	2025-12-16 05:00:00	1
1359	419	186	2025-12-16 07:11:00	2025-12-16 07:05:00	2
1360	419	187	2025-12-16 09:18:00	2025-12-16 09:11:00	3
1361	419	188	2025-12-16 11:26:00	2025-12-16 11:18:00	4
1362	419	189	2025-12-16 13:35:00	2025-12-16 13:26:00	5
1363	419	190	2025-12-16 15:45:00	2025-12-16 15:35:00	6
1364	419	191	2025-12-16 17:45:00	2025-12-16 17:45:00	7
1365	420	183	2025-12-17 05:05:00	2025-12-17 05:00:00	1
1366	420	184	2025-12-17 11:11:00	2025-12-17 11:05:00	2
1367	420	185	2025-12-17 17:11:00	2025-12-17 17:11:00	3
1368	421	184	2025-12-19 11:05:00	2025-12-19 11:00:00	1
1369	421	185	2025-12-19 15:11:00	2025-12-19 15:05:00	2
1370	421	186	2025-12-19 19:18:00	2025-12-19 19:11:00	3
1371	421	187	2025-12-19 23:18:00	2025-12-19 23:18:00	4
1372	422	185	2025-12-18 05:05:00	2025-12-18 05:00:00	1
1373	422	186	2025-12-18 08:11:00	2025-12-18 08:05:00	2
1374	422	187	2025-12-18 11:18:00	2025-12-18 11:11:00	3
1375	422	188	2025-12-18 14:26:00	2025-12-18 14:18:00	4
1376	422	189	2025-12-18 17:26:00	2025-12-18 17:26:00	5
1377	423	186	2025-12-19 05:05:00	2025-12-19 05:00:00	1
1378	423	187	2025-12-19 07:35:00	2025-12-19 07:29:00	2
1379	423	188	2025-12-19 10:06:00	2025-12-19 09:59:00	3
1380	423	189	2025-12-19 12:38:00	2025-12-19 12:30:00	4
1381	423	190	2025-12-19 15:11:00	2025-12-19 15:02:00	5
1382	423	191	2025-12-19 17:35:00	2025-12-19 17:35:00	6
1383	424	184	2025-12-21 11:05:00	2025-12-21 11:00:00	1
1384	424	185	2025-12-21 13:11:00	2025-12-21 13:05:00	2
1385	424	186	2025-12-21 15:18:00	2025-12-21 15:11:00	3
1386	424	187	2025-12-21 17:26:00	2025-12-21 17:18:00	4
1387	424	188	2025-12-21 19:35:00	2025-12-21 19:26:00	5
1388	424	189	2025-12-21 21:45:00	2025-12-21 21:35:00	6
1389	424	190	2025-12-21 23:45:00	2025-12-21 23:45:00	7
1390	425	188	2025-12-20 05:05:00	2025-12-20 05:00:00	1
1391	425	189	2025-12-20 11:11:00	2025-12-20 11:05:00	2
1392	425	190	2025-12-20 17:11:00	2025-12-20 17:11:00	3
1393	426	183	2025-12-22 11:05:00	2025-12-22 11:00:00	1
1394	426	184	2025-12-22 15:11:00	2025-12-22 15:05:00	2
1395	426	185	2025-12-22 19:18:00	2025-12-22 19:11:00	3
1396	426	186	2025-12-22 23:18:00	2025-12-22 23:18:00	4
1397	427	185	2025-12-21 05:05:00	2025-12-21 05:00:00	1
1398	427	186	2025-12-21 08:11:00	2025-12-21 08:05:00	2
1399	427	187	2025-12-21 11:18:00	2025-12-21 11:11:00	3
1400	427	188	2025-12-21 14:26:00	2025-12-21 14:18:00	4
1401	427	189	2025-12-21 17:26:00	2025-12-21 17:26:00	5
1402	428	183	2025-12-22 05:05:00	2025-12-22 05:00:00	1
1403	428	184	2025-12-22 07:35:00	2025-12-22 07:29:00	2
1404	428	185	2025-12-22 10:06:00	2025-12-22 09:59:00	3
1405	428	186	2025-12-22 12:38:00	2025-12-22 12:30:00	4
1406	428	187	2025-12-22 15:11:00	2025-12-22 15:02:00	5
1407	428	188	2025-12-22 17:35:00	2025-12-22 17:35:00	6
1408	429	183	2025-12-23 05:05:00	2025-12-23 05:00:00	1
1409	429	184	2025-12-23 07:11:00	2025-12-23 07:05:00	2
1410	429	185	2025-12-23 09:18:00	2025-12-23 09:11:00	3
1411	429	186	2025-12-23 11:26:00	2025-12-23 11:18:00	4
1412	429	187	2025-12-23 13:35:00	2025-12-23 13:26:00	5
1413	429	188	2025-12-23 15:45:00	2025-12-23 15:35:00	6
1414	429	189	2025-12-23 17:45:00	2025-12-23 17:45:00	7
1415	430	186	2025-12-24 05:05:00	2025-12-24 05:00:00	1
1416	430	187	2025-12-24 11:11:00	2025-12-24 11:05:00	2
1417	430	188	2025-12-24 17:11:00	2025-12-24 17:11:00	3
1418	410	143	2025-02-25 12:23:00	2025-02-20 12:20:00	1
1419	51	144	5000-02-04 10:00:00	2005-03-24 10:00:00	1
\.


--
-- TOC entry 4969 (class 0 OID 16426)
-- Dependencies: 227
-- Data for Name: train; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.train (id, number, locomotive_id, max_speed) FROM stdin;
51	Train_51	50	131
103	Lavlinsky_train_36	2	36
195	001А	157	160
196	002А	158	140
197	003С	159	200
198	004Р	160	120
199	Сапсан	161	250
200	Ласточка	162	160
201	008Щ	163	100
202	009Я	164	110
203	РЭКС-1	165	140
204	РЭКС-2	166	140
206	001A	177	160
207	002B	178	140
208	003C	179	200
209	004D	180	120
210	SAPSAN-1	181	250
211	LASTOCHKA-1	182	160
212	008E	183	100
213	009F	184	110
214	REX-1	185	140
215	REX-2	186	140
217	JNJ3	197	160
218	LA2	198	140
219	003AD	199	200
220	R2D2	200	120
221	SADA-12	201	250
222	WQ2	202	160
223	12S	203	100
224	213K	204	110
225	REX-3	205	140
226	REX-10	206	140
4	Train_4	16	84
\.


--
-- TOC entry 4970 (class 0 OID 16434)
-- Dependencies: 228
-- Data for Name: train_carriage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.train_carriage (id, train_id, type) FROM stdin;
1202	103	freight
1645	195	passenger
1646	195	sleeping
4	4	passenger
1647	195	sleeping
1648	195	passenger
1649	195	sleeping
1650	196	sleeping
1651	196	sleeping
1652	196	passenger
1653	196	sleeping
1654	196	sleeping
1655	196	passenger
1656	197	sleeping
1657	197	passenger
1658	197	sleeping
1659	197	sleeping
1660	197	passenger
1661	197	sleeping
1662	197	sleeping
1663	198	passenger
1664	198	sleeping
1665	198	sleeping
1666	198	passenger
1667	198	sleeping
1668	198	sleeping
1669	198	passenger
1670	198	sleeping
1671	199	sleeping
1672	199	sleeping
1673	199	passenger
1674	199	sleeping
1675	199	sleeping
1676	200	sleeping
1677	200	passenger
1678	200	sleeping
1679	200	sleeping
1680	200	passenger
1681	200	sleeping
1682	201	passenger
1683	201	sleeping
1684	201	sleeping
1685	201	passenger
1686	201	sleeping
1687	201	sleeping
1688	201	passenger
1689	202	sleeping
1690	202	sleeping
1691	202	passenger
1692	202	sleeping
51	51	sleeping
1693	202	sleeping
1694	202	passenger
1695	202	sleeping
1696	202	sleeping
1697	203	sleeping
1698	203	passenger
1699	203	sleeping
1700	203	sleeping
1701	203	passenger
1702	204	passenger
1703	204	sleeping
1704	204	sleeping
1705	204	passenger
1706	204	sleeping
1707	204	sleeping
2695	226	sleeping
2696	226	passenger
2697	226	sleeping
2698	226	sleeping
2699	226	passenger
2700	226	sleeping
2701	226	sleeping
2702	226	passenger
2703	226	sleeping
2704	226	sleeping
2705	226	passenger
2706	226	sleeping
2707	226	sleeping
2708	226	passenger
2709	226	sleeping
2710	226	sleeping
2711	226	passenger
2712	226	sleeping
2713	226	sleeping
2714	226	passenger
2715	226	sleeping
2716	226	sleeping
2717	226	passenger
2718	226	sleeping
2719	226	sleeping
2720	226	passenger
2721	226	sleeping
2722	226	sleeping
2723	226	passenger
2724	226	sleeping
2725	226	sleeping
2726	226	passenger
2727	226	sleeping
2728	226	sleeping
2729	226	passenger
2730	226	sleeping
2731	226	sleeping
104	4	passenger
2732	226	passenger
2733	226	sleeping
2734	226	sleeping
2735	226	passenger
2736	226	sleeping
2737	226	sleeping
2738	226	passenger
2739	226	sleeping
2740	226	sleeping
2741	226	passenger
2742	226	sleeping
2743	226	sleeping
2744	226	passenger
2745	226	sleeping
2746	226	sleeping
2747	226	passenger
2748	226	sleeping
2749	226	sleeping
2750	226	passenger
2751	226	sleeping
2752	226	sleeping
2753	226	passenger
2754	226	sleeping
2755	226	sleeping
2756	226	passenger
2757	226	sleeping
2758	226	sleeping
2759	226	passenger
2760	226	sleeping
2761	226	sleeping
2762	226	passenger
2763	226	sleeping
2764	226	sleeping
2765	226	passenger
2766	226	sleeping
2767	226	sleeping
2768	226	passenger
2769	226	sleeping
2770	226	sleeping
151	51	sleeping
1708	206	passenger
1709	206	sleeping
1710	206	sleeping
1711	206	passenger
1712	206	sleeping
1713	207	sleeping
1714	207	sleeping
1715	207	passenger
1716	207	sleeping
1717	207	sleeping
1718	207	passenger
1719	208	sleeping
1720	208	passenger
1721	208	sleeping
1722	208	sleeping
1723	208	passenger
1724	208	sleeping
1725	208	sleeping
1726	209	passenger
1727	209	sleeping
1728	209	sleeping
1729	209	passenger
1730	209	sleeping
1731	209	sleeping
1732	209	passenger
1733	209	sleeping
1734	210	sleeping
1735	210	sleeping
1736	210	passenger
1737	210	sleeping
1738	210	sleeping
1739	211	sleeping
1740	211	passenger
1741	211	sleeping
1742	211	sleeping
1743	211	passenger
1744	211	sleeping
1745	212	passenger
1746	212	sleeping
1747	212	sleeping
204	4	passenger
1748	212	passenger
1749	212	sleeping
1750	212	sleeping
1751	212	passenger
1752	213	sleeping
1753	213	sleeping
1754	213	passenger
1755	213	sleeping
1756	213	sleeping
1757	213	passenger
1758	213	sleeping
1759	213	sleeping
1760	214	sleeping
1761	214	passenger
1762	214	sleeping
1763	214	sleeping
1764	214	passenger
1765	215	passenger
1766	215	sleeping
1767	215	sleeping
1768	215	passenger
1769	215	sleeping
1770	215	sleeping
251	51	sleeping
304	4	passenger
1771	217	sleeping
1772	217	passenger
1773	217	sleeping
1774	217	sleeping
1775	217	passenger
1776	217	sleeping
1777	217	sleeping
1778	217	passenger
1779	217	sleeping
1780	217	sleeping
1781	217	passenger
1782	217	sleeping
1783	217	sleeping
1784	217	passenger
1785	217	sleeping
1786	217	sleeping
1787	217	passenger
1788	217	sleeping
1789	217	sleeping
1790	217	passenger
1791	217	sleeping
1792	217	sleeping
1793	217	passenger
1794	217	sleeping
351	51	sleeping
1795	217	sleeping
1796	217	passenger
1797	217	sleeping
1798	217	sleeping
1799	217	passenger
1800	217	sleeping
1801	217	sleeping
1802	217	passenger
1803	217	sleeping
1804	217	sleeping
1805	217	passenger
1806	217	sleeping
1807	217	sleeping
1808	217	passenger
1809	217	sleeping
1810	217	sleeping
1811	217	passenger
1812	217	sleeping
1813	217	sleeping
1814	217	passenger
1815	217	sleeping
1816	217	sleeping
1817	217	passenger
1818	217	sleeping
1819	217	sleeping
1820	217	passenger
1821	217	sleeping
1822	217	sleeping
1823	217	passenger
1824	217	sleeping
1825	217	sleeping
1826	217	passenger
1827	217	sleeping
1828	217	sleeping
1829	217	passenger
1830	217	sleeping
1831	217	sleeping
1832	217	passenger
1833	217	sleeping
1834	217	sleeping
1835	217	passenger
1836	217	sleeping
1837	217	sleeping
1838	217	passenger
1839	217	sleeping
1840	217	sleeping
1841	217	passenger
1842	217	sleeping
1843	217	sleeping
1844	217	passenger
1845	217	sleeping
1846	217	sleeping
404	4	passenger
1847	217	passenger
1848	217	sleeping
1849	217	sleeping
1850	217	passenger
1851	217	sleeping
1852	217	sleeping
1853	217	passenger
1854	217	sleeping
1855	217	sleeping
1856	217	passenger
1857	217	sleeping
1858	217	sleeping
1859	217	passenger
1860	217	sleeping
1861	217	sleeping
1862	217	passenger
1863	217	sleeping
1864	217	sleeping
1865	217	passenger
1866	217	sleeping
1867	217	sleeping
1868	217	passenger
1869	217	sleeping
1870	217	sleeping
1871	218	passenger
1872	218	sleeping
1873	218	sleeping
1874	218	passenger
1875	218	sleeping
1876	218	sleeping
1877	218	passenger
1878	218	sleeping
1879	218	sleeping
1880	218	passenger
1881	218	sleeping
1882	218	sleeping
1883	218	passenger
1884	218	sleeping
1885	218	sleeping
1886	218	passenger
1887	218	sleeping
1888	218	sleeping
1889	218	passenger
1890	218	sleeping
1891	218	sleeping
1892	218	passenger
451	51	sleeping
1893	218	sleeping
1894	218	sleeping
1895	218	passenger
1896	218	sleeping
1897	218	sleeping
1898	218	passenger
1899	218	sleeping
1900	218	sleeping
1901	218	passenger
1902	218	sleeping
1903	218	sleeping
1904	218	passenger
1905	218	sleeping
1906	218	sleeping
1907	218	passenger
1908	218	sleeping
1909	218	sleeping
1910	218	passenger
1911	218	sleeping
1912	218	sleeping
1913	218	passenger
1914	218	sleeping
1915	218	sleeping
1916	218	passenger
1917	218	sleeping
1918	218	sleeping
1919	218	passenger
1920	218	sleeping
1921	218	sleeping
1922	218	passenger
1923	218	sleeping
1924	218	sleeping
1925	218	passenger
1926	218	sleeping
1927	218	sleeping
1928	218	passenger
1929	218	sleeping
1930	218	sleeping
1931	218	passenger
1932	218	sleeping
1933	218	sleeping
1934	218	passenger
1935	218	sleeping
1936	218	sleeping
1937	218	passenger
1938	218	sleeping
504	4	passenger
1939	218	sleeping
1940	218	passenger
1941	218	sleeping
1942	218	sleeping
1943	218	passenger
1944	218	sleeping
1945	218	sleeping
1946	218	passenger
1947	218	sleeping
1948	218	sleeping
1949	218	passenger
1950	218	sleeping
1951	218	sleeping
1952	218	passenger
1953	218	sleeping
1954	218	sleeping
1955	218	passenger
1956	218	sleeping
1957	218	sleeping
1958	218	passenger
1959	218	sleeping
1960	218	sleeping
1961	218	passenger
1962	218	sleeping
1963	218	sleeping
1964	218	passenger
1965	218	sleeping
1966	218	sleeping
1967	218	passenger
1968	218	sleeping
1969	218	sleeping
1970	218	passenger
1971	219	sleeping
1972	219	sleeping
1973	219	passenger
1974	219	sleeping
1975	219	sleeping
1976	219	passenger
1977	219	sleeping
1978	219	sleeping
1979	219	passenger
1980	219	sleeping
1981	219	sleeping
1982	219	passenger
1983	219	sleeping
1984	219	sleeping
551	51	sleeping
1985	219	passenger
1986	219	sleeping
1987	219	sleeping
1988	219	passenger
1989	219	sleeping
1990	219	sleeping
1991	219	passenger
1992	219	sleeping
1993	219	sleeping
1994	219	passenger
1995	219	sleeping
1996	219	sleeping
1997	219	passenger
1998	219	sleeping
1999	219	sleeping
2000	219	passenger
2001	219	sleeping
2002	219	sleeping
2003	219	passenger
2004	219	sleeping
2005	219	sleeping
2006	219	passenger
2007	219	sleeping
2008	219	sleeping
2009	219	passenger
2010	219	sleeping
2011	219	sleeping
2012	219	passenger
2013	219	sleeping
2014	219	sleeping
2015	219	passenger
2016	219	sleeping
2017	219	sleeping
2018	219	passenger
2019	219	sleeping
2020	219	sleeping
2021	219	passenger
2022	219	sleeping
2023	219	sleeping
2024	219	passenger
2025	219	sleeping
2026	219	sleeping
2027	219	passenger
2028	219	sleeping
2029	219	sleeping
2030	219	passenger
2031	219	sleeping
2032	219	sleeping
2033	219	passenger
2034	219	sleeping
2035	219	sleeping
2036	219	passenger
604	4	passenger
2037	219	sleeping
2038	219	sleeping
2039	219	passenger
2040	219	sleeping
2041	219	sleeping
2042	219	passenger
2043	219	sleeping
2044	219	sleeping
2045	219	passenger
2046	219	sleeping
2047	219	sleeping
2048	219	passenger
2049	219	sleeping
2050	219	sleeping
2051	219	passenger
2052	219	sleeping
2053	219	sleeping
2054	219	passenger
2055	219	sleeping
2056	219	sleeping
2057	219	passenger
2058	219	sleeping
2059	219	sleeping
2060	219	passenger
2061	219	sleeping
2062	219	sleeping
2063	219	passenger
2064	219	sleeping
2065	219	sleeping
2066	219	passenger
2067	219	sleeping
2068	219	sleeping
2069	219	passenger
2070	219	sleeping
2071	220	sleeping
2072	220	passenger
2073	220	sleeping
2074	220	sleeping
2075	220	passenger
2076	220	sleeping
651	51	sleeping
2077	220	sleeping
2078	220	passenger
2079	220	sleeping
2080	220	sleeping
2081	220	passenger
2082	220	sleeping
2083	220	sleeping
2084	220	passenger
2085	220	sleeping
2086	220	sleeping
2087	220	passenger
2088	220	sleeping
2089	220	sleeping
2090	220	passenger
2091	220	sleeping
2092	220	sleeping
2093	220	passenger
2094	220	sleeping
2095	220	sleeping
2096	220	passenger
2097	220	sleeping
2098	220	sleeping
2099	220	passenger
2100	220	sleeping
2101	220	sleeping
2102	220	passenger
2103	220	sleeping
2104	220	sleeping
2105	220	passenger
2106	220	sleeping
2107	220	sleeping
2108	220	passenger
2109	220	sleeping
2110	220	sleeping
2111	220	passenger
2112	220	sleeping
2113	220	sleeping
2114	220	passenger
2115	220	sleeping
2116	220	sleeping
2117	220	passenger
2118	220	sleeping
2119	220	sleeping
2120	220	passenger
2121	220	sleeping
2122	220	sleeping
2123	220	passenger
2124	220	sleeping
2125	220	sleeping
2126	220	passenger
2127	220	sleeping
704	4	passenger
2128	220	sleeping
2129	220	passenger
2130	220	sleeping
2131	220	sleeping
2132	220	passenger
2133	220	sleeping
2134	220	sleeping
2135	220	passenger
2136	220	sleeping
2137	220	sleeping
2138	220	passenger
2139	220	sleeping
2140	220	sleeping
2141	220	passenger
2142	220	sleeping
2143	220	sleeping
2144	220	passenger
2145	220	sleeping
2146	220	sleeping
2147	220	passenger
2148	220	sleeping
2149	220	sleeping
2150	220	passenger
2151	220	sleeping
2152	220	sleeping
2153	220	passenger
2154	220	sleeping
2155	220	sleeping
2156	220	passenger
2157	220	sleeping
2158	220	sleeping
2159	220	passenger
2160	220	sleeping
2161	220	sleeping
2162	220	passenger
2163	220	sleeping
2164	220	sleeping
2165	220	passenger
2166	220	sleeping
2167	220	sleeping
2168	220	passenger
2169	220	sleeping
2170	220	sleeping
2171	221	passenger
2172	221	sleeping
2173	221	sleeping
751	51	sleeping
2174	221	passenger
2175	221	sleeping
2176	221	sleeping
2177	221	passenger
2178	221	sleeping
2179	221	sleeping
2180	221	passenger
2181	221	sleeping
2182	221	sleeping
2183	221	passenger
2184	221	sleeping
2185	221	sleeping
2186	221	passenger
2187	221	sleeping
2188	221	sleeping
2189	221	passenger
2190	221	sleeping
2191	221	sleeping
2192	221	passenger
2193	221	sleeping
2194	221	sleeping
2195	221	passenger
2196	221	sleeping
2197	221	sleeping
2198	221	passenger
2199	221	sleeping
2200	221	sleeping
2201	221	passenger
2202	221	sleeping
2203	221	sleeping
2204	221	passenger
2205	221	sleeping
2206	221	sleeping
2207	221	passenger
2208	221	sleeping
2209	221	sleeping
2210	221	passenger
2211	221	sleeping
2212	221	sleeping
2213	221	passenger
2214	221	sleeping
2215	221	sleeping
2216	221	passenger
2217	221	sleeping
2218	221	sleeping
2219	221	passenger
2220	221	sleeping
2221	221	sleeping
2222	221	passenger
2223	221	sleeping
2224	221	sleeping
2225	221	passenger
804	4	passenger
2226	221	sleeping
2227	221	sleeping
2228	221	passenger
2229	221	sleeping
2230	221	sleeping
2231	221	passenger
2232	221	sleeping
2233	221	sleeping
2234	221	passenger
2235	221	sleeping
2236	221	sleeping
2237	221	passenger
2238	221	sleeping
2239	221	sleeping
2240	221	passenger
2241	221	sleeping
2242	221	sleeping
2243	221	passenger
2244	221	sleeping
2245	221	sleeping
2246	221	passenger
2247	221	sleeping
2248	221	sleeping
2249	221	passenger
2250	221	sleeping
2251	221	sleeping
2252	221	passenger
2253	221	sleeping
2254	221	sleeping
2255	221	passenger
2256	221	sleeping
2257	221	sleeping
2258	221	passenger
2259	221	sleeping
2260	221	sleeping
2261	221	passenger
2262	221	sleeping
2263	221	sleeping
2264	221	passenger
2265	221	sleeping
851	51	sleeping
2266	221	sleeping
2267	221	passenger
2268	221	sleeping
2269	221	sleeping
2270	221	passenger
2271	222	sleeping
2272	222	sleeping
2273	222	passenger
2274	222	sleeping
2275	222	sleeping
2276	222	passenger
2277	222	sleeping
2278	222	sleeping
2279	222	passenger
2280	222	sleeping
2281	222	sleeping
2282	222	passenger
2283	222	sleeping
2284	222	sleeping
2285	222	passenger
2286	222	sleeping
2287	222	sleeping
2288	222	passenger
2289	222	sleeping
2290	222	sleeping
2291	222	passenger
2292	222	sleeping
2293	222	sleeping
2294	222	passenger
2295	222	sleeping
2296	222	sleeping
2297	222	passenger
2298	222	sleeping
2299	222	sleeping
2300	222	passenger
2301	222	sleeping
2302	222	sleeping
2303	222	passenger
2304	222	sleeping
2305	222	sleeping
2306	222	passenger
2307	222	sleeping
2308	222	sleeping
2309	222	passenger
2310	222	sleeping
2311	222	sleeping
2312	222	passenger
2313	222	sleeping
2314	222	sleeping
2315	222	passenger
2316	222	sleeping
2317	222	sleeping
904	4	passenger
2318	222	passenger
2319	222	sleeping
2320	222	sleeping
2321	222	passenger
2322	222	sleeping
2323	222	sleeping
2324	222	passenger
2325	222	sleeping
2326	222	sleeping
2327	222	passenger
2328	222	sleeping
2329	222	sleeping
2330	222	passenger
2331	222	sleeping
2332	222	sleeping
2333	222	passenger
2334	222	sleeping
2335	222	sleeping
2336	222	passenger
2337	222	sleeping
2338	222	sleeping
2339	222	passenger
2340	222	sleeping
2341	222	sleeping
2342	222	passenger
2343	222	sleeping
2344	222	sleeping
2345	222	passenger
2346	222	sleeping
2347	222	sleeping
2348	222	passenger
2349	222	sleeping
2350	222	sleeping
2351	222	passenger
2352	222	sleeping
2353	222	sleeping
2354	222	passenger
2355	222	sleeping
2356	222	sleeping
2357	222	passenger
2358	222	sleeping
2359	222	sleeping
2360	222	passenger
2361	222	sleeping
2362	222	sleeping
2363	222	passenger
951	51	sleeping
2364	222	sleeping
2365	222	sleeping
2366	222	passenger
2367	222	sleeping
2368	222	sleeping
2369	222	passenger
2370	222	sleeping
2371	223	sleeping
2372	223	passenger
2373	223	sleeping
2374	223	sleeping
2375	223	passenger
2376	223	sleeping
2377	223	sleeping
2378	223	passenger
2379	223	sleeping
2380	223	sleeping
2381	223	passenger
2382	223	sleeping
2383	223	sleeping
2384	223	passenger
2385	223	sleeping
2386	223	sleeping
2387	223	passenger
2388	223	sleeping
2389	223	sleeping
2390	223	passenger
2391	223	sleeping
2392	223	sleeping
2393	223	passenger
2394	223	sleeping
2395	223	sleeping
2396	223	passenger
2397	223	sleeping
2398	223	sleeping
2399	223	passenger
2400	223	sleeping
2401	223	sleeping
2402	223	passenger
2403	223	sleeping
2404	223	sleeping
2405	223	passenger
2406	223	sleeping
2407	223	sleeping
2408	223	passenger
2409	223	sleeping
1004	4	passenger
2410	223	sleeping
2411	223	passenger
2412	223	sleeping
2413	223	sleeping
2414	223	passenger
2415	223	sleeping
2416	223	sleeping
2417	223	passenger
2418	223	sleeping
2419	223	sleeping
2420	223	passenger
2421	223	sleeping
2422	223	sleeping
2423	223	passenger
2424	223	sleeping
2425	223	sleeping
2426	223	passenger
2427	223	sleeping
2428	223	sleeping
2429	223	passenger
2430	223	sleeping
2431	223	sleeping
2432	223	passenger
2433	223	sleeping
2434	223	sleeping
2435	223	passenger
2436	223	sleeping
2437	223	sleeping
2438	223	passenger
2439	223	sleeping
2440	223	sleeping
2441	223	passenger
2442	223	sleeping
2443	223	sleeping
2444	223	passenger
2445	223	sleeping
2446	223	sleeping
2447	223	passenger
2448	223	sleeping
2449	223	sleeping
2450	223	passenger
2451	223	sleeping
2452	223	sleeping
2453	223	passenger
2454	223	sleeping
2455	223	sleeping
1051	51	sleeping
2456	223	passenger
2457	223	sleeping
2458	223	sleeping
2459	223	passenger
2460	223	sleeping
2461	223	sleeping
2462	223	passenger
2463	223	sleeping
2464	223	sleeping
2465	223	passenger
2466	223	sleeping
2467	223	sleeping
2468	223	passenger
2469	223	sleeping
2470	223	sleeping
2471	224	passenger
2472	224	sleeping
2473	224	sleeping
2474	224	passenger
2475	224	sleeping
2476	224	sleeping
2477	224	passenger
2478	224	sleeping
2479	224	sleeping
2480	224	passenger
2481	224	sleeping
2482	224	sleeping
2483	224	passenger
2484	224	sleeping
2485	224	sleeping
2486	224	passenger
2487	224	sleeping
2488	224	sleeping
2489	224	passenger
2490	224	sleeping
2491	224	sleeping
2492	224	passenger
2493	224	sleeping
2494	224	sleeping
2495	224	passenger
2496	224	sleeping
2497	224	sleeping
2498	224	passenger
2499	224	sleeping
2500	224	sleeping
2501	224	passenger
2502	224	sleeping
2503	224	sleeping
2504	224	passenger
2505	224	sleeping
2506	224	sleeping
2507	224	passenger
1104	4	passenger
2508	224	sleeping
2509	224	sleeping
2510	224	passenger
2511	224	sleeping
2512	224	sleeping
2513	224	passenger
2514	224	sleeping
2515	224	sleeping
2516	224	passenger
2517	224	sleeping
2518	224	sleeping
2519	224	passenger
2520	224	sleeping
2521	224	sleeping
2522	224	passenger
2523	224	sleeping
2524	224	sleeping
2525	224	passenger
2526	224	sleeping
2527	224	sleeping
2528	224	passenger
2529	224	sleeping
2530	224	sleeping
2531	224	passenger
2532	224	sleeping
2533	224	sleeping
2534	224	passenger
2535	224	sleeping
2536	224	sleeping
2537	224	passenger
2538	224	sleeping
2539	224	sleeping
2540	224	passenger
2541	224	sleeping
2542	224	sleeping
2543	224	passenger
2544	224	sleeping
2545	224	sleeping
2546	224	passenger
2547	224	sleeping
1151	51	sleeping
2548	224	sleeping
2549	224	passenger
2550	224	sleeping
2551	224	sleeping
2552	224	passenger
2553	224	sleeping
2554	224	sleeping
2555	224	passenger
2556	224	sleeping
2557	224	sleeping
2558	224	passenger
2559	224	sleeping
2560	224	sleeping
2561	224	passenger
2562	224	sleeping
2563	224	sleeping
2564	224	passenger
2565	224	sleeping
2566	224	sleeping
2567	224	passenger
2568	224	sleeping
2569	224	sleeping
2570	224	passenger
2571	225	sleeping
2572	225	sleeping
2573	225	passenger
2574	225	sleeping
2575	225	sleeping
2576	225	passenger
2577	225	sleeping
2578	225	sleeping
2579	225	passenger
2580	225	sleeping
2581	225	sleeping
2582	225	passenger
2583	225	sleeping
2584	225	sleeping
2585	225	passenger
2586	225	sleeping
2587	225	sleeping
2588	225	passenger
2589	225	sleeping
2590	225	sleeping
2591	225	passenger
2592	225	sleeping
2593	225	sleeping
2594	225	passenger
2595	225	sleeping
2596	225	sleeping
2597	225	passenger
2598	225	sleeping
2599	225	sleeping
2600	225	passenger
2601	225	sleeping
2602	225	sleeping
2603	225	passenger
2604	225	sleeping
2605	225	sleeping
2606	225	passenger
2607	225	sleeping
2608	225	sleeping
2609	225	passenger
2610	225	sleeping
2611	225	sleeping
2612	225	passenger
2613	225	sleeping
2614	225	sleeping
2615	225	passenger
2616	225	sleeping
2617	225	sleeping
2618	225	passenger
2619	225	sleeping
2620	225	sleeping
2621	225	passenger
2622	225	sleeping
2623	225	sleeping
2624	225	passenger
2625	225	sleeping
2626	225	sleeping
2627	225	passenger
2628	225	sleeping
2629	225	sleeping
2630	225	passenger
2631	225	sleeping
2632	225	sleeping
2633	225	passenger
2634	225	sleeping
2635	225	sleeping
2636	225	passenger
2637	225	sleeping
2638	225	sleeping
2639	225	passenger
2640	225	sleeping
2641	225	sleeping
2642	225	passenger
2643	225	sleeping
2644	225	sleeping
2645	225	passenger
2646	225	sleeping
2647	225	sleeping
2648	225	passenger
2649	225	sleeping
2650	225	sleeping
2651	225	passenger
2652	225	sleeping
2653	225	sleeping
2654	225	passenger
2655	225	sleeping
2656	225	sleeping
2657	225	passenger
2658	225	sleeping
2659	225	sleeping
2660	225	passenger
2661	225	sleeping
2662	225	sleeping
2663	225	passenger
2664	225	sleeping
2665	225	sleeping
2666	225	passenger
2667	225	sleeping
2668	225	sleeping
2669	225	passenger
2670	225	sleeping
2671	226	sleeping
2672	226	passenger
2673	226	sleeping
2674	226	sleeping
2675	226	passenger
2676	226	sleeping
2677	226	sleeping
2678	226	passenger
2679	226	sleeping
2680	226	sleeping
2681	226	passenger
2682	226	sleeping
2683	226	sleeping
2684	226	passenger
2685	226	sleeping
2686	226	sleeping
2687	226	passenger
2688	226	sleeping
2689	226	sleeping
2690	226	passenger
2691	226	sleeping
2692	226	sleeping
2693	226	passenger
2694	226	sleeping
\.


--
-- TOC entry 4984 (class 0 OID 0)
-- Dependencies: 220
-- Name: locomotive_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.locomotive_id_seq', 206, true);


--
-- TOC entry 4985 (class 0 OID 0)
-- Dependencies: 222
-- Name: schedule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.schedule_id_seq', 431, true);


--
-- TOC entry 4986 (class 0 OID 0)
-- Dependencies: 225
-- Name: station_entry_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.station_entry_id_seq', 1421, true);


--
-- TOC entry 4987 (class 0 OID 0)
-- Dependencies: 226
-- Name: station_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.station_id_seq', 192, true);


--
-- TOC entry 4988 (class 0 OID 0)
-- Dependencies: 229
-- Name: train_carriage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.train_carriage_id_seq', 2770, true);


--
-- TOC entry 4989 (class 0 OID 0)
-- Dependencies: 230
-- Name: train_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.train_id_seq', 226, true);


--
-- TOC entry 4794 (class 2606 OID 16450)
-- Name: locomotive locomotive_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locomotive
    ADD CONSTRAINT locomotive_pkey PRIMARY KEY (id);


--
-- TOC entry 4798 (class 2606 OID 16452)
-- Name: schedule schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_pkey PRIMARY KEY (id);


--
-- TOC entry 4802 (class 2606 OID 16454)
-- Name: station_entry station_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station_entry
    ADD CONSTRAINT station_entry_pkey PRIMARY KEY (id);


--
-- TOC entry 4800 (class 2606 OID 16456)
-- Name: station station_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station
    ADD CONSTRAINT station_pkey PRIMARY KEY (id);


--
-- TOC entry 4808 (class 2606 OID 16458)
-- Name: train_carriage train_carriage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train_carriage
    ADD CONSTRAINT train_carriage_pkey PRIMARY KEY (id);


--
-- TOC entry 4804 (class 2606 OID 16460)
-- Name: train train_number_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train
    ADD CONSTRAINT train_number_key UNIQUE (number);


--
-- TOC entry 4806 (class 2606 OID 16462)
-- Name: train train_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train
    ADD CONSTRAINT train_pkey PRIMARY KEY (id);


--
-- TOC entry 4795 (class 1259 OID 16463)
-- Name: idx_schedule_departure; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_schedule_departure ON public.schedule USING btree (departure_time);


--
-- TOC entry 4796 (class 1259 OID 16464)
-- Name: idx_schedule_train; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_schedule_train ON public.schedule USING btree (train_id);


--
-- TOC entry 4809 (class 2606 OID 16465)
-- Name: schedule schedule_train_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule
    ADD CONSTRAINT schedule_train_id_fkey FOREIGN KEY (train_id) REFERENCES public.train(id) ON DELETE CASCADE;


--
-- TOC entry 4810 (class 2606 OID 16470)
-- Name: station_entry station_entry_schedule_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station_entry
    ADD CONSTRAINT station_entry_schedule_id_fkey FOREIGN KEY (schedule_id) REFERENCES public.schedule(id) ON DELETE CASCADE;


--
-- TOC entry 4811 (class 2606 OID 16475)
-- Name: station_entry station_entry_station_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station_entry
    ADD CONSTRAINT station_entry_station_id_fkey FOREIGN KEY (station_id) REFERENCES public.station(id);


--
-- TOC entry 4813 (class 2606 OID 16480)
-- Name: train_carriage train_carriage_train_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train_carriage
    ADD CONSTRAINT train_carriage_train_id_fkey FOREIGN KEY (train_id) REFERENCES public.train(id) ON DELETE CASCADE;


--
-- TOC entry 4812 (class 2606 OID 16485)
-- Name: train train_locomotive_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.train
    ADD CONSTRAINT train_locomotive_id_fkey FOREIGN KEY (locomotive_id) REFERENCES public.locomotive(id) ON DELETE SET NULL;


package com.gerp.shared.generic.custom;

import com.gerp.shared.generic.api.pagination.filter.CustomFilter;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.Collection;
import java.util.List;

public class AppUtil {

    /**
     * Checks if is collection empty.
     *
     * @param collection the collection
     * @return true, if is collection empty
     */
    private static boolean isCollectionEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is object empty.
     *
     * @param object the object
     * @return true, if is object empty
     */
    public static boolean isObjectEmpty(Object object) {
        if (object == null) return true;
        else if (object instanceof String) {
            if (((String) object).trim().length() == 0) {
                return true;
            }
        } else if (object instanceof Collection) {
            return isCollectionEmpty((Collection<?>) object);
        }
        return false;
    }

    /**
     * Builds the paginated query.
     *
     * @param baseQuery the base query
     * @param data      the pagination criteria
     * @return the string
     */
    public static String buildPaginatedQuery(String baseQuery, GetRowsRequest data) {
        StringBuilder sb = new StringBuilder("SELECT BASEINFO.* FROM ( #BASE_QUERY# #WHERE_CLAUSE#) BASEINFO ");
        String finalQuery = null;
        if (!AppUtil.isObjectEmpty(data)) {
            finalQuery = sb.toString().replaceAll("#BASE_QUERY#", baseQuery)
                    .replaceAll("#WHERE_CLAUSE#",
                            ((AppUtil.isObjectEmpty(data.getFilterModel())) ? "" : " ") + getFilterByClause(data.getFilterModel()));
        }
        return (null == finalQuery) ? baseQuery : finalQuery;
    }

    public static String buildPaginatedQueryCount(String baseQuery, GetRowsRequest data) {
        StringBuilder sb = new StringBuilder("SELECT count(BASEINFO.*) FROM ( #BASE_QUERY# #WHERE_CLAUSE#) BASEINFO ");
        String finalQuery = null;
        if (!AppUtil.isObjectEmpty(data)) {
            finalQuery = sb.toString().replaceAll("#BASE_QUERY#", baseQuery)
                    .replaceAll("#WHERE_CLAUSE#",
                            ((AppUtil.isObjectEmpty(data.getFilterModel())) ? "" : "	 ") + getFilterByClause(data.getFilterModel()));
        }
        return (null == finalQuery) ? baseQuery : finalQuery;
    }

//	public Page<AP2_OfficeAuditPlan> findItems(String z, Pageable pageable, GetRowsRequest data) {
//		String zz = AppUtil.buildPaginatedQuery(z, data);
//		String zzz = AppUtil.buildPaginatedQueryCount(z, data);
//
//		System.out.println(zz);
//		System.out.println(zzz);
//
//		Query query = entityManager.createNativeQuery(zz, AP2_OfficeAuditPlan.class)
//				.setParameter(1,data.getFiscalYear())
//				.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
//				.setMaxResults(pageable.getPageSize())
//				.
//				;
//		List<AP2_OfficeAuditPlan> fooList = query.getResultList();
//
//		Query queryTotal = entityManager.createNativeQuery(zzz).setParameter(1, data.getFiscalYear());
//		BigInteger countResult = (BigInteger) queryTotal.getSingleResult();
//		System.out.println(countResult);
//		return new PageImpl<>(fooList, pageable, countResult.longValueExact());
//	}

    private static String getFilterByClause(List<CustomFilter> filterModel) {

        StringBuilder fbsb = null;

        if (filterModel != null && !filterModel.isEmpty()) {
            for (CustomFilter x : filterModel) {
//				if(null == fbsb) {
                fbsb = new StringBuilder();
//					fbsb.append(BRKT_OPN);
//
//					fbsb.append(SPACE)
//							.append(BRKT_OPN)
//							.append(x.getField())
//							.append(LIKE_PREFIX)
//							.append(x.getSearchValue())
//							.append(LIKE_SUFFIX)
//							.append(BRKT_CLS);

//				} else {
                fbsb.append(
//                            x.isGlobalSearch() ? OR :
                        AND)
                        .append(BRKT_OPN)
                        .append(x.getField())
                        .append(LIKE_PREFIX)
                        .append(x.getSearchValue())
                        .append(LIKE_SUFFIX)
                        .append(BRKT_CLS);

//				}
            }
//			fbsb.append(BRKT_CLS);
        }

        return (null == fbsb) ? BLANK : fbsb.toString();
    }

    /**
     * The Constant BLANK.
     */
    private static final String BLANK = "";

    /**
     * The Constant SPACE.
     */
    private static final String SPACE = " ";

    /**
     * The Constant LIKE_PREFIX.
     */
    private static final String LIKE_PREFIX = " LIKE '%";
    private static final String LIKE_PREFIX2 = " LIKE '";

    /**
     * The Constant LIKE_SUFFIX.
     */
    private static final String LIKE_SUFFIX = "%' ";

    /**
     * The Constant AND.
     */
    private static final String AND = " AND ";

    /**
     * The Constant OR.
     */
    private static final String OR = " OR ";

    /**
     * The Constant ORDER_BY.
     */
    private static final String ORDER_BY = " ORDER BY ";

    private static final String BRKT_OPN = " ( ";

    private static final String BRKT_CLS = " ) ";

    /**
     * The Constant COMMA.
     */
    private static final String COMMA = " , ";

    /**
     * The Constant PAGE_NO.
     */
    public static final String PAGE_NO = "start";

    /**
     * The Constant PAGE_SIZE.
     */
    public static final String PAGE_SIZE = "length";

    /**
     * The Constant DRAW.
     */
    public static final String DRAW = "draw";

    public static String checkNullReturnSkip(String data) {
        if (data == null || data.trim().equals(""))
            return "___";
        return data;
    }
}

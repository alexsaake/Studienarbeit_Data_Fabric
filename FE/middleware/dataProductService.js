export async function getDataProducts(axios)
{
  return await axios.$get("api/Gateway/DataProducts");
}

export async function getDataProduct(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}`);
}

export async function deleteDataProduct(axios, id)
{
  return await axios.$delete(`api/Gateway/DataProduct/${id}`);
}
export async function insertDataProduct(axios, data)
{
  return await axios.$post(`api/Gateway/DataProduct`, data);
}
export async function updateDataProduct(axios, data, id)
{
  return await axios.$patch(`api/Gateway/DataProduct/${id}`, data);
}
export async function insertMapsData(axios, id, data) {
  return await axios.$post(`api/Gateway/DataProduct/${id}/Data/MapsData/Filter`, data);
}
export async function getDataProductImage(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Image`);
}

export async function getDataProductData(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Data`);
}
export async function getDataProductDataAll(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/DataAll`);
}

export async function getDataProductCategories(axios)
{
  return await axios.$get(`api/Gateway/DataProducts/Categories`);
}
export async function getDataProductAccessRights(axios)
{
  return await axios.$get(`api/Gateway/DataProducts/AccessRights`);
}
export async function getDataProductInsights(axios, id, param)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${id}/Data/Insights`,{
      params: {
        filterKeys: param.filterKeys,
        filterValues: param.filterValues,
      }
    });
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function getInsightTypes(axios)
{
  return await axios.$get(`api/Gateway/DataProducts/Insights/Types`);
}
export async function getInsightFilterTypes(axios)
{
  return await axios.$get(`api/Gateway/DataProducts/Insights/FilterTypes`);
}
export async function getDataProductInsightFilters(axios, id)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${id}/Data/Insights/Filter`);
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function getDataProductInsightFilterValues(axios, id, filterId, param)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${id}/Data/Insights/Filter/${filterId}`,{
      params: {
        areaFilter: param.areaFilter,
      }
      });
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProductRatings(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Ratings`);
}


export async function setDataProductRating(axios, id, title, comment, rating)
{
  return await axios.$post(`api/Gateway/DataProduct/${id}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}
export async function getDataProductAvgRatings(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Ratings/Averages`);
}

export async function updateDataProductRating(axios, id, title, comment, rating)
{
  return await axios.$put(`api/Gateway/DataProduct/${id}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function deleteDataProductRating(axios, id)
{
  return await axios.request(`api/Gateway/DataProduct/${id}/Rating`, {data: null, method: 'delete'});
}

export async function getDataProductRatingCanSubmit(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Rating/CanSubmit`);
}

export async function getDataProductRatingMaxLengths(axios)
{
  return await axios.$get(`api/Gateway/DataProduct/Rating/MaxLengths`);
}
export async function getDataProducts(axios)
{
  return await axios.$get("api/Gateway/DataProducts");
}

export async function getDataProduct(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}`);
}

export async function deleteDataProduct(axios, shortKey)
{
  return await axios.$delete(`api/Gateway/DataProduct/${shortKey}`);
}
export async function insertDataProduct(axios, data)
{
  return await axios.$post(`api/Gateway/DataProduct`, data);
}
export async function insertInsights(axios, shortKey, data)
{
  return await axios.$post(`api/Gateway/DataProduct/${shortKey}/Data/Insights`, data);
}
export async function insertInsightFilter(axios, shortKey, data) {
  return await axios.$post(`api/Gateway/DataProduct/${shortKey}/Data/Insights/Filter`, data);
}
export async function insertMapsData(axios, shortKey, data) {
  return await axios.$post(`api/Gateway/DataProduct/${shortKey}/Data/MapsData/Filter`, data);
}
export async function getDataProductImage(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Image`);
}

export async function getDataProductData(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data`);
}

export async function getDataProductCategories(axios)
{
  return await axios.$get(`api/Gateway/DataProducts/Categories`);
}
export async function getDataProductAccessRights(axios)
{
  return await axios.$get(`api/Gateway/DataProducts/AccessRights`);
}
export async function getDataProductInsights(axios, shortKey, param)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data/Insights`,{
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
export async function getDataProductInsightFilters(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data/Insights/Filter`);
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function getDataProductInsightFilterValues(axios, shortKey, filterId, param)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data/Insights/Filter/${filterId}`,{
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

export async function getDataProductRatings(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Ratings`);
}


export async function setDataProductRating(axios, shortKey, title, comment, rating)
{
  return await axios.$post(`api/Gateway/DataProduct/${shortKey}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}
export async function getDataProductAvgRatings(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Ratings/Averages`);
}

export async function updateDataProductRating(axios, shortKey, title, comment, rating)
{
  return await axios.$put(`api/Gateway/DataProduct/${shortKey}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function deleteDataProductRating(axios, shortKey)
{
  return await axios.request(`api/Gateway/DataProduct/${shortKey}/Rating`, {data: null, method: 'delete'});
}

export async function getDataProductRatingCanSubmit(axios, shortKey)
{
  return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Rating/CanSubmit`);
}

export async function getDataProductRatingMaxLengths(axios)
{
  return await axios.$get(`api/Gateway/DataProduct/Rating/MaxLengths`);
}
export async function getDataProducts(axios)
{
  return await axios.$get("api/Gateway/DataProducts");
}

export async function getDataProduct(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}`);
}

export async function getDataProductDetails(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Details`);
}

export async function deleteDataProduct(axios, id)
{
  return await axios.$delete(`api/Gateway/DataProduct/${id}`);
}
export async function insertDataProduct(axios, data)
{
  return await axios.$post(`api/Gateway/DataProduct`, data);
}
export async function uploadDataProductImage(axios, dataProductId, imageFile) {
  const formData = new FormData();
  formData.append('image', imageFile);

  try {
    const response = await axios.$post(`api/Gateway/DataProduct/${dataProductId}/image`, formData, {
    });

    return response;
  } catch (error) {
    console.error(error);
    throw error;
  }
}

export async function getDataProductImage(axios, dataProductId) {
  try {
    // Make a request to the endpoint to get the image
    // Setting the responseType to 'blob' since we are expecting binary data
    const response = await axios.get(`api/Gateway/DataProduct/${dataProductId}/image`, { responseType: 'blob' });

    // Creating a URL for the blob data
    const imageUrl = URL.createObjectURL(response.data);

    // Returning the URL, which can be used as the src attribute of an <img> tag
    return imageUrl;
  } catch (error) {
    console.error('Error fetching data product image:', error);
    // Handle the error as needed
    throw error;
  }
}
export async function updateDataProduct(axios, data, id)
{
  return await axios.$patch(`api/Gateway/DataProduct/${id}`, data);
}

export async function getDataProductData(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Data`);
}
export async function getDataProductCharts(axios, id)
{
  return await axios.$get(`api/Gateway/DataProduct/${id}/Data/Charts`);
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

export async function getDataProductRatings(axios, dataProductId)
{
  return await axios.$get(`api/Gateway/DataProduct/${dataProductId}/Ratings`);
}

export async function getDataProductRating(axios, ratingId)
{
  return await axios.$get(`api/Gateway/DataProduct/Rating/${ratingId}`);
}

export async function setDataProductRating(axios, dataProductId, title, comment, rating)
{
  return await axios.$post(`api/Gateway/DataProduct/${dataProductId}/Rating`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function getDataProductAvgRating(axios, dataProductId)
{
  return await axios.$get(`api/Gateway/DataProduct/${dataProductId}/Ratings/Average`);
}

export async function updateDataProductRating(axios, ratingId, title, comment, rating)
{
  return await axios.$put(`api/Gateway/DataProduct/Rating/${ratingId}`,
      {
        title,
        comment,
        rating
      }
  );
}

export async function deleteDataProductRating(axios, ratingId)
{
  return await axios.request(`api/Gateway/DataProduct/Rating/${ratingId}`, {data: null, method: 'delete'});
}

export async function getDataProductRatingCanSubmit(axios, dataProductId)
{
  return await axios.$get(`api/Gateway/DataProduct/${dataProductId}/Rating/CanSubmit`);
}

export async function getDataProductRatingMaxLengths(axios)
{
  return await axios.$get(`api/Gateway/DataProduct/Rating/MaxLengths`);
}
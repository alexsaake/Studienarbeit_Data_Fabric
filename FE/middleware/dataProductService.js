export async function getDataProducts(axios)
{
  try
  {
    return await axios.$get("api/Gateway/DataProducts");
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProduct(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}`);
  }
  catch (error)
  {
    console.log(error);
  }
}


export async function getDataProductImage(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Image`);
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProductData(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data`);
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function getDataProductInsights(axios, shortKey, param)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data/Insights`,{
      params: {
        areaFilter: param.areaFilter,
        dateFromFilter: param.dateFromFilter,
        dateToFilter: param.dateToFilter
      }
    });
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function getDataProductInsightsCities(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Data/Cities`);
  }
  catch (error)
  {
    console.log(error);
  }
}

export async function getDataProductRatings(axios, shortKey)
{
  try
  {
    return await axios.$get(`api/Gateway/DataProduct/${shortKey}/Ratings`);
  }
  catch (error)
  {
    console.log(error);
  }
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

export async function getDataProductRatingCommentMaxLength(axios)
{
  return await axios.$get(`api/Gateway/DataProduct/Rating/Comment/MaxLength`);
}
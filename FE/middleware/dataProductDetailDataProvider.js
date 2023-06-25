export default async function (axios, shortKey)
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

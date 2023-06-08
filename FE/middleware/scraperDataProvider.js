export  default function scraperDataProvider(){
  return null;
}
export async function get(axios)
{
  try
  {
    return await axios.$get("api/Gateway/ScraperData");
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function post(axios, cityName)
{
  try
  {
    return await axios.$post("api/Gateway/ScraperData",null,{
      headers:{
        'Content-Type':'application/json'
      },
      params:{
        'city':cityName
      }
    });
  }
  catch (error)
  {
    console.log(error);
  }
}
export async function patch(axios,data)
{
  try
  {
    return await axios.$patch("api/Gateway/ScraperData",null,{
      params:data,
      headers: {
        'Content-Type': 'application/json'
      }
    }
  );
  }
  catch (error)
  {
    console.log(error);
  }
}

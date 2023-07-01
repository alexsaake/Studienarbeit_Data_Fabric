export default async function (axios, firstName, lastName, userName, email, password)
{
    return await axios.post(`api/Gateway/auth/register`,
        {
            firstName,
            lastName,
            userName,
            email,
            password
        });
}
